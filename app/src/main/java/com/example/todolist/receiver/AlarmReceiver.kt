package com.example.todolist.receiver

import android.app.NotificationManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.core.content.ContextCompat
import com.example.todolist.sendNotification

class AlarmReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {

        val message = intent!!.getStringExtra("message")
        val taskId = intent!!.getLongExtra("taskId", 1)
        val notificationManager = ContextCompat.getSystemService(
            context!!,
            NotificationManager::class.java
        ) as NotificationManager
        notificationManager.sendNotification(message, context, taskId)
        intent!!.removeExtra("message")
        intent!!.removeExtra("taskId")

    }

}