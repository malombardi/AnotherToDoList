package com.mlprogramming.anothertodolist.utils

import android.app.Activity
import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import com.google.gson.Gson
import com.mlprogramming.anothertodolist.alarm.AlarmReceiver
import com.mlprogramming.anothertodolist.alarm.AlarmReceiver.Companion.INTENT_EXTRA
import com.mlprogramming.anothertodolist.model.ToDoTask
import com.mlprogramming.anothertodolist.place.GeofenceReceiver
import com.mlprogramming.anothertodolist.place.GeofenceReceiver.Companion.INTENT_EXTRA_GEOFENCE
import com.mlprogramming.anothertodolist.utils.UiUtils.Companion.generateRequestCode
import java.util.*


class NotificationUtils {

    fun setNotification(toDoTask: ToDoTask, timeInMilliSeconds: Long, activity: Context) {

        if (timeInMilliSeconds > 0) {

            val alarmManager = activity.getSystemService(Activity.ALARM_SERVICE) as AlarmManager
            val alarmIntent = Intent(activity.applicationContext, AlarmReceiver::class.java)

            val gson = Gson()
            val json = gson.toJson(toDoTask)

            alarmIntent.putExtra(INTENT_EXTRA, json)

            val calendar = Calendar.getInstance()
            calendar.timeInMillis = timeInMilliSeconds

            val pendingIntent = PendingIntent.getBroadcast(
                activity,
                generateRequestCode(toDoTask.internalId!!, timeInMilliSeconds),
                alarmIntent,
                PendingIntent.FLAG_UPDATE_CURRENT
            )
            alarmManager.set(AlarmManager.RTC_WAKEUP, calendar.timeInMillis, pendingIntent)

        }
    }

    fun setNotification(id: Int, title: String, context: Context) {
        val alarmManager = context.getSystemService(Activity.ALARM_SERVICE) as AlarmManager
        val alarmIntent = Intent(context.applicationContext, GeofenceReceiver::class.java)

        alarmIntent.putExtra(INTENT_EXTRA_GEOFENCE, title)

        val calendar = Calendar.getInstance()

        val pendingIntent = PendingIntent.getBroadcast(
            context,
            generateRequestCode(id, calendar.timeInMillis),
            alarmIntent,
            PendingIntent.FLAG_UPDATE_CURRENT
        )
        alarmManager.set(AlarmManager.RTC_WAKEUP, calendar.timeInMillis, pendingIntent)
    }

    fun cancelNotification(toDoTask: ToDoTask, timeInMilliSeconds: Long, activity: Activity) {
        val alarmManager = activity.getSystemService(Activity.ALARM_SERVICE) as AlarmManager
        val alarmIntent = Intent(activity.applicationContext, AlarmReceiver::class.java)
        val pendingIntent = PendingIntent.getBroadcast(
            activity,
            generateRequestCode(toDoTask.internalId!!, timeInMilliSeconds),
            alarmIntent,
            PendingIntent.FLAG_UPDATE_CURRENT
        )
        alarmManager.cancel(pendingIntent);
    }
}