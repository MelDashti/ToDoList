package com.example.todolist

import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat
import androidx.navigation.NavDeepLinkBuilder
import com.example.todolist.receiver.CompletedReceiver
import com.example.todolist.receiver.SnoozeReceiver
import java.util.*

private var NOTIFICATION_ID = 0
private val REQUEST_CODE = 0
private val FLAGS = 0
fun NotificationManager.sendNotification(
    messageBody: String,
    applicationContext: Context,
    taskId: Long
) {

    //Snooze Intent
    val snoozeIntent = Intent(applicationContext, SnoozeReceiver::class.java)
    val snoozePendingIntent: PendingIntent =
        PendingIntent.getBroadcast(applicationContext, REQUEST_CODE, snoozeIntent, FLAGS)
//    val contentIntent = Intent(applicationContext, MainActivity::class.java)
//    val contentPendingIntent = PendingIntent.getActivity(
//        applicationContext,
//        NOTIFICATION_ID,
//        contentIntent,
//        PendingIntent.FLAG_UPDATE_CURRENT
//    )

    //Content action Intent
    val contentPendingIntent =
        NavDeepLinkBuilder(applicationContext).setGraph(R.navigation.navigation)
            .setDestination(R.id.aboutFragment).createPendingIntent()

    //Completed Intent
    val completedIntent = Intent(applicationContext, CompletedReceiver::class.java)
    completedIntent.putExtra("taskId", taskId)
    val completedPendingIntent =
        PendingIntent.getBroadcast(applicationContext, REQUEST_CODE, completedIntent, FLAGS)


    val builder = NotificationCompat.Builder(
        applicationContext,
        applicationContext.getString(R.string.reminder_notification_channel_id)
    )
        .setSmallIcon(R.drawable.ic_group_1)
        .setContentTitle(applicationContext.getString(R.string.notification_title))
        .setContentText(messageBody)
        .setContentIntent(contentPendingIntent)
        .setAutoCancel(true)
        .addAction(R.drawable.ic_group_1, "Completed", completedPendingIntent)
        .addAction(R.drawable.ic_group_1, "Snooze", snoozePendingIntent)
        .setPriority(NotificationCompat.PRIORITY_HIGH)

    //call notify to send the notification
    var m = (Date().time / 1000L % Int.MAX_VALUE).toInt()

    notify(m, builder.build())
    //snooze action
}