package com.mlprogramming.anothertodolist.place

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.google.android.gms.location.Geofence
import com.google.android.gms.location.GeofencingEvent
import com.mlprogramming.anothertodolist.AnotherToDoListApplication
import com.mlprogramming.anothertodolist.place.PlaceViewModel.Companion.ACTION_GEOFENCE_EVENT
import com.mlprogramming.anothertodolist.place.PlaceViewModel.Companion.FENCE_SEPARATOR
import com.mlprogramming.anothertodolist.utils.NotificationUtils

class GeofenceBroadcastReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {

        if (intent.action == ACTION_GEOFENCE_EVENT) {
            val storageManager =
                (context.applicationContext as AnotherToDoListApplication).appComponent.storageManager()
            val userManager =
                (context.applicationContext as AnotherToDoListApplication).appComponent.userManager()
            if (userManager.userComponent == null) {
                userManager.loginUserLoggedIn()
            }

            val geofencingEvent = GeofencingEvent.fromIntent(intent)

            if (geofencingEvent.hasError()) {
                return
            }

            if (geofencingEvent.geofenceTransition == Geofence.GEOFENCE_TRANSITION_ENTER) {

                val fenceId = when {
                    geofencingEvent.triggeringGeofences.isNotEmpty() ->
                        geofencingEvent.triggeringGeofences[0].requestId
                    else -> {
                        return
                    }
                }

                val fenceData = fenceId.split(FENCE_SEPARATOR)
                NotificationUtils().setNotification(fenceData[0].toInt(), fenceData[1], context)

            }
        }
    }

}

const val TAG = "GeofenceReceiver"