package com.mlprogramming.anothertodolist.place

import android.app.NotificationManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.core.content.ContextCompat
import com.google.android.gms.location.Geofence
import com.google.android.gms.location.GeofencingEvent
import com.mlprogramming.anothertodolist.model.ToDoTask
import com.mlprogramming.anothertodolist.place.PlaceViewModel.Companion.ACTION_GEOFENCE_EVENT
import com.mlprogramming.anothertodolist.utils.NotificationUtils
import java.util.*

class GeofenceBroadcastReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action == ACTION_GEOFENCE_EVENT) {
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

                //TODO find the task
                val foundTask = ToDoTask()
                //fenceId

                // Unknown Geofences aren't helpful to us
                if (foundTask == null) {
                    return
                }

                NotificationUtils().setNotification(
                    foundTask,
                    Calendar.getInstance().timeInMillis,
                    context
                )

            }
        }
    }
}

const val TAG = "GeofenceReceiver"