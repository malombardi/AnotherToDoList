package com.mlprogramming.anothertodolist.place

import android.Manifest
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.location.Location
import android.os.Looper
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
    val needPermissions: Boolean? = null
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
}

class PlaceViewModel(
    private val activity: FragmentActivity,
    private val task: ToDoTask
) : ViewModel() {
    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient

    companion object {
        const val REQUEST_LOCATION_PERMISSION = 1
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

            is UiIntent.PermissionsGranted -> enableMyLocation()
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
                    needPermissions = null
                )
            }

            is Command.RequestPermissions -> {
                state.copy(
                    needPermissions = true
                )
            }
        }
    }

    private fun initializeVars() {
        fusedLocationProviderClient =
            LocationServices.getFusedLocationProviderClient(activity)
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