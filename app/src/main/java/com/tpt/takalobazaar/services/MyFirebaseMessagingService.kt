package com.tpt.takalobazaar.services

import android.Manifest
import android.app.PendingIntent
import android.app.TaskStackBuilder
import android.content.Intent
import android.content.pm.PackageManager
import android.util.Log
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.tpt.takalobazaar.MainActivity
import com.tpt.takalobazaar.R

class MyFirebaseMessagingService : FirebaseMessagingService() {

    override fun onNewToken(token: String) {
        super.onNewToken(token)
        Log.d("FCM", "Refreshed token: $token")
        // Here you can send the token to your server if needed
    }

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        super.onMessageReceived(remoteMessage)

        remoteMessage.notification?.let { notification ->
            val title = notification.title ?: "Notification"
            val message = notification.body ?: "You have a new message"

            // Broadcast the message within the app
            val intent = Intent("com.tpt.takalobazaar.FCM_MESSAGE")
            intent.putExtra("title", title)
            intent.putExtra("message", message)
            sendBroadcast(intent)
        }
    }

    private fun sendNotification(title: String, message: String, clickAction: String) {
        val intent = Intent(this, MainActivity::class.java).apply {
            action = clickAction
            addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        }

        val pendingIntent: PendingIntent = TaskStackBuilder.create(this).run {
            addNextIntentWithParentStack(intent)
            getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE)
        }

        val notificationBuilder = NotificationCompat.Builder(this, "your_channel_id")
            .setSmallIcon(R.drawable.ic_notifications)
            .setContentTitle(title)
            .setContentText(message)
            .setAutoCancel(true)
            .setContentIntent(pendingIntent)
            .setPriority(NotificationCompat.PRIORITY_HIGH)

        val notificationManager = NotificationManagerCompat.from(this)

        // Check if the notification permission is granted
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) == PackageManager.PERMISSION_GRANTED) {
            // Permission is granted, show the notification
            notificationManager.notify(0, notificationBuilder.build())
        } else {
            // Permission is not granted, handle accordingly
            Log.w("FCM", "Notification permission is not granted. Cannot send notification.")
            // You might want to request the permission here if this is in an activity
        }
    }
}