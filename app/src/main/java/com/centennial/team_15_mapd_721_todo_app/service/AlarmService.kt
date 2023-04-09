package com.centennial.team_15_mapd_721_todo_app.service

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.media.MediaPlayer
import android.media.RingtoneManager
import android.os.Build
import com.centennial.team_15_mapd_721_todo_app.models.MyConstants
import com.centennial.team_15_mapd_721_todo_app.models.TaskModel
import com.centennial.team_15_mapd_721_todo_app.notification.MyNotification
import com.google.gson.Gson

class MyAlarmReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {

        if(intent.action == MyConstants.ALARMTRIGGERACTION) {
            AlarmService.playSound(context)

            val task = Gson().fromJson(intent.getStringExtra("task"), TaskModel::class.java)
            AlarmService.notifications!!.showNotification(
                "Due Task: " + task.name!!,
                task.note!!,
                null,
            )
            // Cancel alarm because pending intent is not automatically removed
            AlarmService.cancelAlarm(context, task)

            context.sendBroadcast(Intent(MyConstants.ALARMTRIGGERIDACTION2))
        }
        else if(intent.action == MyConstants.STOPALARMACTION || intent.action == MyConstants.NOTIFICATIONCLICKEDACTION){
            AlarmService.stopSound()
        }

    }
}

class AlarmService {

    companion object {
        var notifications: MyNotification? = null
        private var mediaPlayer: MediaPlayer? = null

        fun initialize(context:Context){
            notifications = MyNotification(context)
        }

        fun setAlarm(context: Context, taskModel: TaskModel) {
                cancelAlarm(context,taskModel)
                val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
                val alarmIntent = Intent(context, MyAlarmReceiver::class.java).apply {
                    action = MyConstants.ALARMTRIGGERACTION
                    putExtra("task", Gson().toJson(taskModel)) // Pass the extra data
                }
                val pendingIntent = PendingIntent.getBroadcast(context, taskModel.id.hashCode(), alarmIntent, PendingIntent.FLAG_IMMUTABLE)

                // Set the alarm
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, taskModel.dueDate!!.time, pendingIntent)
                } else {
                    alarmManager.setExact(AlarmManager.RTC_WAKEUP, taskModel.dueDate!!.time, pendingIntent)
                }
        }


        fun cancelAlarm(context: Context, taskModel: TaskModel) {
            val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
            val alarmIntent = Intent(context, MyAlarmReceiver::class.java).apply {
                action = MyConstants.ALARMTRIGGERACTION
                putExtra("task", Gson().toJson(taskModel)) // Pass the extra data
            }

            // Retrieve the PendingIntent with the same requestCode (taskModel.hashCode()) and FLAG_IMMUTABLE
            val pendingIntent = PendingIntent.getBroadcast(
                context,
                taskModel.id.hashCode(),
                alarmIntent,
                PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_NO_CREATE
            )

            // Cancel the alarm if the PendingIntent exists
            if (pendingIntent != null) {
                alarmManager.cancel(pendingIntent)
                pendingIntent.cancel()
            }
        }

        fun playSound(context: Context) {
            // Stop any previous playback
            stopSound()

            // Create a new MediaPlayer instance
            mediaPlayer = MediaPlayer().apply {
                setDataSource(context, RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM))
                prepare()
                start()
            }
        }

        fun stopSound() {
            // Check if MediaPlayer is currently playing
            mediaPlayer?.let { mediaPlayer ->
                if (mediaPlayer.isPlaying) {
                    // Stop playback and release MediaPlayer resources
                    mediaPlayer.stop()
                    mediaPlayer.release()
                    this.mediaPlayer = null
                }
            }
        }

    }

}