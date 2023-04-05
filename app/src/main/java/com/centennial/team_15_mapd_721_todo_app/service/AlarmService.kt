package com.centennial.team_15_mapd_721_todo_app.service

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import com.centennial.team_15_mapd_721_todo_app.models.MyConstants
import com.centennial.team_15_mapd_721_todo_app.notification.MyNotification
import java.util.*

class MyAlarmReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        AlarmService.notifications!!.showNotification("Title","Message",null)
        Utils.showMessage(context,"This is a test")
    }
}

class AlarmService {

    companion object {
        var notifications: MyNotification? = null

        fun initialize(context:Context){
            notifications = MyNotification(context)
        }

        fun setAlarm(context: Context, date: Date, requestCode: Int) {
                val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
                val alarmIntent = Intent(context, MyAlarmReceiver::class.java).apply {
                    action = MyConstants.ALARMID
                }
                val pendingIntent = PendingIntent.getBroadcast(context, requestCode, alarmIntent, PendingIntent.FLAG_IMMUTABLE)

                // Set the alarm
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, date.time, pendingIntent)
                } else {
                    alarmManager.setExact(AlarmManager.RTC_WAKEUP, date.time, pendingIntent)
                }
        }


        fun cancelAlarm(context: Context, requestCode: Int, intent: Intent) {

            val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

            val alarmIntent = Intent(context, MyAlarmReceiver::class.java).apply {
                action = MyConstants.ALARMID
            }

            val pendingIntent = PendingIntent.getBroadcast(context, requestCode, alarmIntent, PendingIntent.FLAG_IMMUTABLE)
            alarmManager.cancel(pendingIntent)
            pendingIntent.cancel()
        }

    }

}