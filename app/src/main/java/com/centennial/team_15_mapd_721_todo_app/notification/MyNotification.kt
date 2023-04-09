package com.centennial.team_15_mapd_721_todo_app.notification

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import com.centennial.team_15_mapd_721_todo_app.R
import com.centennial.team_15_mapd_721_todo_app.models.MyConstants
import com.centennial.team_15_mapd_721_todo_app.service.MyAlarmReceiver
import com.centennial.team_15_mapd_721_todo_app.ui.main.MainActivity

class MyNotification(private val context: Context) {
    private val CHANNEL_ID = "my_channel_id"
    private val CHANNEL_NAME = "Todo App"
    private var NOTIFICATION_ID = 1


    fun showNotification(title: String, message: String, data:Bundle?) {



        NOTIFICATION_ID++

        val intent = Intent(context, MainActivity::class.java)
        intent.action = MyConstants.NOTIFICATIONCLICKEDACTION
        val pendingIntent = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            PendingIntent.getActivity(
                context,
                0,
                intent,
                PendingIntent.FLAG_MUTABLE
            )
        } else {
            PendingIntent.getActivity(
                context,
                0,
                intent,
                0
            )
        }

        val deleteIntent = Intent(context, MyAlarmReceiver::class.java)
        deleteIntent.action = MyConstants.STOPALARMACTION
        val deletePendingIntent = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            PendingIntent.getBroadcast(context, 0, deleteIntent, PendingIntent.FLAG_MUTABLE)
        } else {
            PendingIntent.getBroadcast(context, 0, deleteIntent, 0)
        }

        var notificationManager:NotificationManager? = null

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val importance = NotificationManager.IMPORTANCE_HIGH // Importance level for the notification channel
            val channel = NotificationChannel(CHANNEL_ID, CHANNEL_NAME, importance)
            notificationManager =  context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }

        val notificationBuilder = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            Notification.Builder(context, CHANNEL_ID)
        } else {
            Notification.Builder(context)
        }


        notificationBuilder
            .setSmallIcon(R.mipmap.ic_launcher_round) // Set the small icon
            .setContentTitle(title)
            .setContentText(message)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .setDeleteIntent(deletePendingIntent)
            .setPriority(Notification.PRIORITY_DEFAULT)



        notificationManager = notificationManager ?: context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.notify(NOTIFICATION_ID, notificationBuilder.build())
    }

}