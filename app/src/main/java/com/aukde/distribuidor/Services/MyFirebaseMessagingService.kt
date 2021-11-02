package com.aukde.distribuidor.Services

import android.app.Notification
import android.app.PendingIntent
import android.content.Intent
import android.media.RingtoneManager
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import com.aukde.distribuidor.Notifications.NotificationHelper
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage

class MyFirebaseMessagingService : FirebaseMessagingService() {

    private val NOTIFICATION_CODE = 100

    override fun onNewToken(s: String) {
        super.onNewToken(s)
    }

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        super.onMessageReceived(remoteMessage)
        val notification = remoteMessage.notification
        val data = remoteMessage.data
        val title = data["title"]
        val body = data["body"]
        val path = data["path"]

        if (title != null) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                showNotificationApiOreo(title, body, path)
            } else {
                showNotification(title, body, path)
            }
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private fun showNotificationApiOreo(title: String, body: String?, path: String?) {
        val intent = PendingIntent.getActivity(baseContext, 0, Intent(), PendingIntent.FLAG_ONE_SHOT)
        val sound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM)
        val notificationHelper = NotificationHelper(baseContext)
        val builder = notificationHelper.getNotification(title, body, intent, sound, path)
        notificationHelper.manager!!.notify(1, builder.build())
    }

    private fun showNotification(title: String, body: String?, path: String?) {
        val intent = PendingIntent.getActivity(baseContext, 0, Intent(), PendingIntent.FLAG_ONE_SHOT)
        val sound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM)
        val notificationHelper = NotificationHelper(baseContext)
        val builder = notificationHelper.getNotificationOldApi(title, (body)!!, (path)!!, intent, sound)
        notificationHelper.manager!!.notify(1, builder.build())
    }


}