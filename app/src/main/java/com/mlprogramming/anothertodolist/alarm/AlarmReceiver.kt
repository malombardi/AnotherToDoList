package com.mlprogramming.anothertodolist.alarm

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.mlprogramming.anothertodolist.model.ToDoTask
import com.mlprogramming.anothertodolist.notification.NotificationService

class AlarmReceiver : BroadcastReceiver() {

    companion object{
        const val INTENT_EXTRA = "intent_extra_task"
    }

    override fun onReceive(context: Context, intent: Intent) {

        val service = Intent(context, NotificationService::class.java)
        service.putExtra(
            INTENT_EXTRA,
            intent.getStringExtra(INTENT_EXTRA)
        )

        context.startService(service)
    }

}