package com.mlprogramming.anothertodolist.place

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.mlprogramming.anothertodolist.notification.NotificationService

class GeofenceReceiver : BroadcastReceiver() {

    companion object{
        const val INTENT_EXTRA_GEOFENCE = "intent_extra_geo"
    }

    override fun onReceive(context: Context, intent: Intent) {

        val service = Intent(context, NotificationService::class.java)
        service.putExtra(
            INTENT_EXTRA_GEOFENCE,
            intent.getStringExtra(INTENT_EXTRA_GEOFENCE)
        )

        context.startService(service)
    }

}