package com.mlprogramming.anothertodolist.notification

import android.annotation.SuppressLint
import android.app.*
import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.graphics.Color
import android.media.RingtoneManager
import android.os.Build
import com.google.gson.Gson
import com.mlprogramming.anothertodolist.R
import com.mlprogramming.anothertodolist.alarm.AlarmReceiver
import com.mlprogramming.anothertodolist.main.MainActivity
import com.mlprogramming.anothertodolist.model.ToDoTask

class NotificationService : IntentService("NotificationService") {
    private lateinit var mNotification: Notification
    private val mNotificationId: Int = 1000

    @SuppressLint("NewApi")
    private fun createChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val context = this.applicationContext
            val notificationManager =
                context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

            val importance = NotificationManager.IMPORTANCE_HIGH
            val notificationChannel = NotificationChannel(CHANNEL_ID, CHANNEL_NAME, importance)
            notificationChannel.enableVibration(true)
            notificationChannel.setShowBadge(true)
            notificationChannel.enableLights(true)
            notificationChannel.lightColor = Color.parseColor("#66bb6a")
            notificationChannel.description = getString(R.string.notification_channel_description)
            notificationChannel.lockscreenVisibility = Notification.VISIBILITY_PUBLIC
            notificationManager.createNotificationChannel(notificationChannel)
        }
    }

    companion object {
        const val CHANNEL_ID = "com.mlprogramming.anothertodolist.CHANNEL_ID"
        const val CHANNEL_NAME = "AnotherToDoList Channel"
    }

    override fun onHandleIntent(intent: Intent?) {
        createChannel()

        var task: ToDoTask? = null
        if (intent != null && intent.extras != null) {
            val gson = Gson()
            task = gson.fromJson<ToDoTask>(
                intent.extras!!.getString(AlarmReceiver.INTENT_EXTRA),
                ToDoTask::class.java
            )
        }

        if (task != null) {
            val context = this.applicationContext
            val notificationManager =
                getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            val notifyIntent = Intent(this, MainActivity::class.java)

            val title = task.title
            val description = task.description

            notifyIntent.putExtra("title", title)
            notifyIntent.putExtra("message", description)
            notifyIntent.putExtra("notification", true)

            notifyIntent.flags = Intent.FLAG_ACTIVITY_NEW_TASK

            val pendingIntent = PendingIntent.getActivity(
                context,
                0,
                notifyIntent,
                PendingIntent.FLAG_UPDATE_CURRENT
            )
            val res = this.resources
            val uri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                mNotification = Notification.Builder(this, CHANNEL_ID)
                    .setContentIntent(pendingIntent)
                    .setSmallIcon(R.drawable.ic_alarm_on)
                    .setLargeIcon(BitmapFactory.decodeResource(res, R.mipmap.ic_launcher))
                    .setAutoCancel(true)
                    .setContentTitle(title)
                    .setStyle(
                        Notification.BigTextStyle()
                            .bigText(description)
                    )
                    .setContentText(description).build()
            } else {
                mNotification = Notification.Builder(this)
                    .setContentIntent(pendingIntent)
                    .setSmallIcon(R.drawable.ic_alarm_on)
                    .setLargeIcon(BitmapFactory.decodeResource(res, R.mipmap.ic_launcher))
                    .setAutoCancel(true)
                    .setPriority(Notification.PRIORITY_MAX)
                    .setContentTitle(title)
                    .setStyle(
                        Notification.BigTextStyle()
                            .bigText(description)
                    )
                    .setSound(uri)
                    .setContentText(description).build()
            }
            notificationManager.notify(mNotificationId, mNotification)
        }
    }
}