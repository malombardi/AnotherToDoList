package com.mlprogramming.anothertodolist.place

import android.Manifest
import android.annotation.SuppressLint
import android.app.PendingIntent
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.os.Looper
import android.util.Log
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.android.gms.location.*
import com.mlprogramming.anothertodolist.model.Place
import com.mlprogramming.anothertodolist.model.ToDoTask

data class UiState(
    val navDirection: Any? = null,
    val places: MutableLiveData<ArrayList<Place>>? = null,
    val loading: Boolean? = false,
    val save: Boolean? = false,
    val currentLocation: Location? = null,
    val needPermissions: Boolean? = null,
    val permissionsBlocked: Boolean = false
)

sealed class UiIntent {
    data class RemovePlace(var place: Place) : UiIntent()
    data class AddPlace(var place: Place) : UiIntent()
    object ShowPlaces : UiIntent()
    object Save : UiIntent()
    object Cancel : UiIntent()
    object NavigationCompleted : UiIntent()
    object InitializeVars : UiIntent()
    object PermissionsGranted : UiIntent()
    object PermissionsDenied : UiIntent()
    object RecheckPermissions : UiIntent()
}

sealed class Command {
    data class RemovePlace(var place: Place) : Command()
    data class AddPlace(var place: Place) : Command()
    data class UpdateMyPosition(var location: Location?) : Command()
    object ShowPlaces : Command()
    object Save : Command()
    object Cancel : Command()
    object NavigationCompleted : Command()
    object RequestPermissions : Command()
    object PermissionsDenied : Command()
    object PermissionsGranted : Command()
}

class PlaceViewModel(
    private val activity: FragmentActivity,
    private val task: ToDoTask
) : ViewModel() {
    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient
    val runningQOrLater =
        android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.Q
    private lateinit var geofencingClient: GeofencingClient
    private val geofencePendingIntent: PendingIntent by lazy {

        val intent = Intent(activity, GeofenceBroadcastReceiver::class.java)
        intent.action = ACTION_GEOFENCE_EVENT
        PendingIntent.getBroadcast(activity, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT)
    }

    companion object {
        const val REQUEST_FOREGROUND_AND_BACKGROUND_PERMISSION_RESULT_CODE = 33
        const val REQUEST_FOREGROUND_ONLY_PERMISSIONS_REQUEST_CODE = 34
        const val REQUEST_TURN_DEVICE_LOCATION_ON = 29
        const val LOCATION_PERMISSION_INDEX = 0
        const val BACKGROUND_LOCATION_PERMISSION_INDEX = 1
        internal const val ACTION_GEOFENCE_EVENT =
            "PlaceFragment.action.ACTION_GEOFENCE_EVENT"
        const val GEOFENCE_RADIUS_IN_METERS = 300f
        const val FENCE_SEPARATOR = "FenceSeparator"
        val GEOFENCE_EXPIRATION_IN_MILLISECONDS: Long =
            java.util.concurrent.TimeUnit.DAYS.toMillis(365)
    }

    var places = MutableLiveData<ArrayList<Place>>(task.places?.clone() as ArrayList<Place>?)

    private val _uiState = MutableLiveData<UiState>().apply {
        value = getInitialState()
    }

    val uiState: MutableLiveData<UiState>
        get() = _uiState

    fun onHandleIntent(intent: UiIntent) {
        return when (intent) {
            is UiIntent.ShowPlaces -> onCommand(Command.ShowPlaces)

            is UiIntent.Cancel -> onCommand(Command.Cancel)

            is UiIntent.Save -> onCommand(Command.Save)

            is UiIntent.RemovePlace -> onCommand(Command.RemovePlace(intent.place))

            is UiIntent.AddPlace -> onCommand(Command.AddPlace(intent.place))

            is UiIntent.NavigationCompleted -> onCommand(Command.NavigationCompleted)

            is UiIntent.InitializeVars -> initializeVars()

            is UiIntent.RecheckPermissions -> recheckPermissions()

            is UiIntent.PermissionsGranted -> onCommand(Command.PermissionsGranted)

            is UiIntent.PermissionsDenied -> onCommand(Command.PermissionsDenied)
        }
    }

    private fun onCommand(command: Command) {
        val currentState = _uiState.value ?: return
        _uiState.value = reduce(currentState, command)
    }

    private fun reduce(state: UiState, command: Command): UiState {
        return when (command) {

            is Command.ShowPlaces -> {
                if (places.value == null) {
                    places.value = ArrayList<Place>()
                }
                state.copy(
                    places = places
                )
            }

            is Command.RemovePlace -> {
                places.value!!.remove(command.place)
                state.copy(
                    places = places
                )
            }

            is Command.AddPlace -> {
                if (places.value == null) {
                    places.value = ArrayList<Place>()
                }
                val place = command.place
                place.title = ("Place " + places.value!!.size)

                places.value!!.add(place)
                state.copy(
                    places = places
                )
            }

            is Command.Save -> {
                task.places = places.value

                for (place in places.value!!) {
                    addGeofence(place)
                }
                val fragmentDirections =
                    PlaceFragmentDirections.actionPlaceFragmentToTaskFragment()

                state.copy(
                    navDirection = fragmentDirections,
                    loading = true,
                    save = true
                )
            }

            is Command.Cancel -> {
                val fragmentDirections =
                    PlaceFragmentDirections.actionPlaceFragmentToTaskFragment()

                state.copy(
                    navDirection = fragmentDirections,
                    loading = true
                )
            }

            is Command.NavigationCompleted -> state.copy(navDirection = null)

            is Command.UpdateMyPosition -> {
                state.copy(
                    currentLocation = command.location,
                    needPermissions = null,
                    permissionsBlocked = false
                )
            }

            is Command.RequestPermissions -> {
                state.copy(
                    needPermissions = true
                )
            }

            is Command.PermissionsDenied -> {
                state.copy(
                    needPermissions = null,
                    permissionsBlocked = true
                )
            }

            is Command.PermissionsGranted -> {
                enableMyLocation()
                state.copy(
                    needPermissions = false,
                    permissionsBlocked = false
                )
            }
        }
    }

    private fun initializeVars() {
        if(!this::fusedLocationProviderClient.isInitialized) {
            fusedLocationProviderClient =
                LocationServices.getFusedLocationProviderClient(activity)
            geofencingClient = LocationServices.getGeofencingClient(activity)
        }
        enableMyLocation()
    }

    private fun isPermissionGranted(): Boolean {
        return ContextCompat.checkSelfPermission(
            activity.applicationContext,
            Manifest.permission.ACCESS_COARSE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(
                    activity.applicationContext,
                    Manifest.permission.ACCESS_FINE_LOCATION
                ) == PackageManager.PERMISSION_GRANTED
    }

    private fun recheckPermissions() {
        if (uiState.value?.permissionsBlocked!! && isPermissionGranted()) {
            fusedLocationProviderClient.lastLocation.addOnCompleteListener { task ->
                val location = task.result
                if (location != null) {
                    onCommand(Command.UpdateMyPosition(location))
                } else {
                    requestNewLocationData()
                }
            }
        }
    }

    private fun enableMyLocation() {
        if (isPermissionGranted()) {
            fusedLocationProviderClient.lastLocation.addOnCompleteListener { task ->
                val location = task.result
                if (location != null) {
                    onCommand(Command.UpdateMyPosition(location))
                } else {
                    requestNewLocationData()
                }
            }
        } else {
            onCommand(Command.RequestPermissions)
        }
    }

    private fun addGeofence(place: Place) {
        val requestId = "" + task.internalId!! + FENCE_SEPARATOR + task.title
        val geofence = Geofence.Builder()
            .setRequestId(requestId)
            .setCircularRegion(
                place.latitude!!,
                place.longitude!!,
                GEOFENCE_RADIUS_IN_METERS
            )
            .setExpirationDuration(GEOFENCE_EXPIRATION_IN_MILLISECONDS)
            .setTransitionTypes(Geofence.GEOFENCE_TRANSITION_ENTER)
            .build()

        val geofencingRequest = GeofencingRequest.Builder()
            .setInitialTrigger(GeofencingRequest.INITIAL_TRIGGER_ENTER)
            .addGeofence(geofence)
            .build()

        geofencingClient.removeGeofences(geofencePendingIntent)?.run {
            addOnCompleteListener {
                geofencingClient.addGeofences(geofencingRequest, geofencePendingIntent)?.run {
                    addOnSuccessListener {
                        Log.e(TAG, "success"+geofence.requestId)
                    }
                    addOnFailureListener {
                        if ((it.message != null)) {
                            Log.w(TAG, "Error" + it.stackTrace)
                        }
                    }
                }
            }
        }
    }

    @SuppressLint("MissingPermission")
    private fun requestNewLocationData() {
        val mLocationRequest = LocationRequest()
        mLocationRequest.priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        mLocationRequest.interval = 0
        mLocationRequest.fastestInterval = 0
        mLocationRequest.numUpdates = 1

        fusedLocationProviderClient.requestLocationUpdates(
            mLocationRequest, mLocationCallback,
            Looper.myLooper()
        )
    }

    private val mLocationCallback = object : LocationCallback() {
        override fun onLocationResult(location: LocationResult?) {
            location?.let {
                onCommand(Command.UpdateMyPosition(it.lastLocation))
            }
        }
    }


    private fun getInitialState() = UiState(
        navDirection = null,
        places = places,
        loading = null,
        save = null,
        currentLocation = null,
        needPermissions = null
    )
}