package com.mlprogramming.anothertodolist.place

import android.Manifest
import android.annotation.TargetApi
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.IntentSender
import android.content.pm.PackageManager
import android.location.Location
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.NavDirections
import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.LocationSettingsRequest
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.MapView
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.material.snackbar.Snackbar
import com.mlprogramming.anothertodolist.BuildConfig
import com.mlprogramming.anothertodolist.R
import com.mlprogramming.anothertodolist.main.MainActivity
import com.mlprogramming.anothertodolist.main.Navigator
import com.mlprogramming.anothertodolist.main.SharedViewModel
import com.mlprogramming.anothertodolist.model.Place
import com.mlprogramming.anothertodolist.model.ToDoTask
import com.mlprogramming.anothertodolist.place.PlaceViewModel.Companion.BACKGROUND_LOCATION_PERMISSION_INDEX
import com.mlprogramming.anothertodolist.place.PlaceViewModel.Companion.LOCATION_PERMISSION_INDEX
import com.mlprogramming.anothertodolist.place.PlaceViewModel.Companion.REQUEST_FOREGROUND_AND_BACKGROUND_PERMISSION_RESULT_CODE
import com.mlprogramming.anothertodolist.place.PlaceViewModel.Companion.REQUEST_FOREGROUND_ONLY_PERMISSIONS_REQUEST_CODE
import com.mlprogramming.anothertodolist.place.PlaceViewModel.Companion.REQUEST_TURN_DEVICE_LOCATION_ON
import kotlinx.android.synthetic.main.fragment_place.*

class PlaceFragment : Fragment(), OnMapReadyCallback {
    private lateinit var placeViewModel: PlaceViewModel
    private lateinit var sharedViewModel: SharedViewModel
    private lateinit var navigator: Navigator
    private lateinit var task: ToDoTask
    private var map: GoogleMap? = null
    private lateinit var mapView: MapView

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_place, container, false)
        mapView = view.findViewById(R.id.place_map)
        mapView.onCreate(savedInstanceState)
        mapView.isClickable = true
        mapView.isLongClickable = true
        mapView.getMapAsync(this)
        return view
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        navigator = Navigator((activity as MainActivity).getNavController())

        arguments?.let {
            task = PlaceFragmentArgs.fromBundle(it).task!!
        }

        placeViewModel =
            ViewModelProviders.of(this, PlaceViewModelFactory(requireActivity(), task))
                .get(PlaceViewModel::class.java)

        sharedViewModel = ViewModelProviders.of(this).get(SharedViewModel::class.java)
        sharedViewModel.task.observe(this, Observer<ToDoTask> { data ->
            data?.let {
                task = data
            }
        })

        finishViewSetup()
        setupStateObserver()
        placeViewModel.onHandleIntent(UiIntent.InitializeVars)
    }

    private fun finishViewSetup() {
        cancel.setOnClickListener {
            placeViewModel.onHandleIntent(UiIntent.Cancel)
        }

        save.setOnClickListener {
            placeViewModel.onHandleIntent(UiIntent.Save)
        }
    }

    private fun setupStateObserver() {
        placeViewModel.uiState.observe(this, Observer { state ->
            state.save?.let {
                when (it) {
                    true -> sharedViewModel.updateData(task)
                }
            }
            state.navDirection?.let {
                navigator.navigate(it as NavDirections)
                placeViewModel.onHandleIntent(UiIntent.NavigationCompleted)
            }
            state.places?.let {
                if (map != null) {
                    for (place in it.value!!) {
                        val marker =
                            map!!.addMarker(
                                MarkerOptions()
                                    .position(LatLng(place.latitude!!, place.longitude!!))
                                    .title(place.title!!)
                            )
                        marker.isDraggable = true
                    }
                }
            }
            state.currentLocation?.let {
                moveCameraToLocation(it)
            }
            state.needPermissions?.let {
                if (it) {
                    requestPermissions()
                }
            }
            state.permissionsBlocked?.let {
                if (it){
                    showSnackBar()
                }
            }
        })
    }

    override fun onMapReady(googleMap: GoogleMap) {
        map = googleMap

        map!!.setOnMapLongClickListener { point ->

            val newMarker =
                map!!.addMarker(MarkerOptions().position(point))

            newMarker.remove()
            val place = Place().apply {
                latitude = point.latitude
                longitude = point.longitude
            }
            placeViewModel.onHandleIntent(UiIntent.AddPlace(place))
        }
        placeViewModel.onHandleIntent(UiIntent.ShowPlaces)
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        if (permissions.isNotEmpty()) {
            if (grantResults.isEmpty() ||
                grantResults[LOCATION_PERMISSION_INDEX] == PackageManager.PERMISSION_DENIED ||
                (requestCode == REQUEST_FOREGROUND_AND_BACKGROUND_PERMISSION_RESULT_CODE &&
                        grantResults[BACKGROUND_LOCATION_PERMISSION_INDEX] ==
                        PackageManager.PERMISSION_DENIED)
            ) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    if (neverAskAgainSelected(
                            requireActivity(),
                            Manifest.permission.ACCESS_FINE_LOCATION
                        )
                    ) {
                        placeViewModel.onHandleIntent(UiIntent.PermissionsDenied)
                    } else {
                        setShouldShowStatus(
                            requireContext(),
                            Manifest.permission.ACCESS_FINE_LOCATION
                        )
                        placeViewModel.onHandleIntent(UiIntent.InitializeVars)
                    }
                }
            } else {
                checkDeviceLocationSettingsAndStartGeofence()
            }


            if (grantResults.contains(PackageManager.PERMISSION_GRANTED)) {
                placeViewModel.onHandleIntent(UiIntent.PermissionsGranted)
            }
        }
    }

    private fun showSnackBar() {
        Snackbar.make(
            requireView(),
            R.string.permission_denied_explanation, Snackbar.LENGTH_INDEFINITE
        )
            .setAction(R.string.settings) {
                startActivity(Intent().apply {
                    action = Settings.ACTION_APPLICATION_DETAILS_SETTINGS
                    data =
                        Uri.fromParts("package", BuildConfig.APPLICATION_ID, null)
                    flags = Intent.FLAG_ACTIVITY_NEW_TASK
                })
            }.show()
    }

    private fun moveCameraToLocation(location: Location?) {
        map!!.isMyLocationEnabled = true
        location?.let {
            val myLatLng = LatLng(it.latitude, it.longitude)
            val cameraPosition = CameraPosition.Builder().target(myLatLng).zoom(12f).build()
            map!!.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition))
        }
    }

    private fun requestPermissions() {
        if (foregroundAndBackgroundLocationPermissionApproved()) {
            checkDeviceLocationSettingsAndStartGeofence()
        } else {
            requestForegroundAndBackgroundLocationPermissions()
        }
    }

    private fun checkDeviceLocationSettingsAndStartGeofence(resolve: Boolean = true) {
        val locationRequest = LocationRequest.create().apply {
            priority = LocationRequest.PRIORITY_LOW_POWER
        }
        val builder = LocationSettingsRequest.Builder().addLocationRequest(locationRequest)

        val settingsClient = LocationServices.getSettingsClient(requireActivity())
        val locationSettingsResponseTask =
            settingsClient.checkLocationSettings(builder.build())

        locationSettingsResponseTask.addOnFailureListener { exception ->
            if (exception is ResolvableApiException && resolve) {
                try {
                    exception.startResolutionForResult(
                        activity,
                        REQUEST_TURN_DEVICE_LOCATION_ON
                    )
                } catch (sendEx: IntentSender.SendIntentException) {
                    Log.d(TAG, "Error geting location settings resolution: " + sendEx.message)
                }
            } else {
                Snackbar.make(
                    requireView(),
                    R.string.location_required_error, Snackbar.LENGTH_INDEFINITE
                ).setAction(android.R.string.ok) {
                    checkDeviceLocationSettingsAndStartGeofence()
                }.show()
            }
        }

    }

    @TargetApi(29)
    private fun foregroundAndBackgroundLocationPermissionApproved(): Boolean {
        val foregroundLocationApproved = (
                PackageManager.PERMISSION_GRANTED ==
                        ActivityCompat.checkSelfPermission(
                            requireContext(),
                            Manifest.permission.ACCESS_FINE_LOCATION
                        ))
        val backgroundPermissionApproved =
            if (placeViewModel.runningQOrLater) {
                PackageManager.PERMISSION_GRANTED ==
                        ActivityCompat.checkSelfPermission(
                            requireContext(), Manifest.permission.ACCESS_BACKGROUND_LOCATION
                        )
            } else {
                true
            }
        return foregroundLocationApproved && backgroundPermissionApproved
    }


    @TargetApi(29)
    private fun requestForegroundAndBackgroundLocationPermissions() {
        if (foregroundAndBackgroundLocationPermissionApproved())
            return

        var permissionsArray = arrayOf(Manifest.permission.ACCESS_FINE_LOCATION)

        val resultCode = when {
            placeViewModel.runningQOrLater -> {
                permissionsArray += Manifest.permission.ACCESS_BACKGROUND_LOCATION
                REQUEST_FOREGROUND_AND_BACKGROUND_PERMISSION_RESULT_CODE
            }
            else -> REQUEST_FOREGROUND_ONLY_PERMISSIONS_REQUEST_CODE
        }

        Log.d(TAG, "Request foreground only location permission")
        requestPermissions(
            permissionsArray,
            resultCode
        )
    }

    override fun onResume() {
        super.onResume()
        placeViewModel.onHandleIntent(UiIntent.RecheckPermissions)
        mapView.onResume()
    }

    override fun onPause() {
        super.onPause()
        mapView.onPause()
    }

    override fun onDestroy() {
        super.onDestroy()
        mapView.onDestroy()
    }

    override fun onLowMemory() {
        super.onLowMemory()
        mapView.onLowMemory()
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    fun neverAskAgainSelected(activity: Activity, permission: String?): Boolean {
        val prevShouldShowStatus = getRationaleDisplayStatus(activity, permission)
        val currShouldShowStatus =
            requireActivity().shouldShowRequestPermissionRationale(permission!!)
        return prevShouldShowStatus && !currShouldShowStatus
    }

    private fun getRationaleDisplayStatus(
        context: Context,
        permission: String?
    ): Boolean {
        val genPrefs = context.getSharedPreferences(
            "GENERIC_PREFERENCES",
            Context.MODE_PRIVATE
        )
        return genPrefs.getBoolean(permission, false)
    }

    private fun setShouldShowStatus(
        context: Context,
        permission: String?
    ) {
        val genPrefs = context.getSharedPreferences(
            "GENERIC_PREFERENCES",
            Context.MODE_PRIVATE
        )
        val editor = genPrefs.edit()
        editor.putBoolean(permission, true)
        editor.commit()
    }
}