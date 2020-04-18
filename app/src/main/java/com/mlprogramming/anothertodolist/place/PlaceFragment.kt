package com.mlprogramming.anothertodolist.place

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.NavDirections
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.MapView
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.mlprogramming.anothertodolist.R
import com.mlprogramming.anothertodolist.main.MainActivity
import com.mlprogramming.anothertodolist.main.Navigator
import com.mlprogramming.anothertodolist.main.SharedViewModel
import com.mlprogramming.anothertodolist.model.Place
import com.mlprogramming.anothertodolist.model.ToDoTask
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
            ViewModelProviders.of(this, PlaceViewModelFactory(task))
                .get(PlaceViewModel::class.java)

        sharedViewModel = ViewModelProviders.of(this).get(SharedViewModel::class.java)
        sharedViewModel.task.observe(this, Observer<ToDoTask> { data ->
            data?.let {
                task = data
            }
        })

        finishViewSetup()
        setupStateObserver()
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
/*
        val cameraPosition =
            CameraPosition.Builder().zoom(12f).build()
        map!!.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition))*/
    }

    override fun onResume() {
        super.onResume()
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
}