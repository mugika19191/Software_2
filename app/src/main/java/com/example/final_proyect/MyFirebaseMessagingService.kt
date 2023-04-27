package com.example.final_proyect
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Handler
import android.os.Looper
import android.widget.RemoteViews
import android.widget.Toast
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage

class MyFirebaseMessagingService :FirebaseMessagingService(){
    override fun onNewToken(token: String) {
        super.onNewToken(token)
    }
    override fun onMessageReceived (remoteMessage: RemoteMessage) {
        Looper.prepare()
        Handler().post {
            Toast.makeText (baseContext, remoteMessage.notification?.body, Toast.LENGTH_LONG).show()
        }
        Looper.loop()
    }
}