package com.example.tbaycity

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Intent
import android.os.Build
import android.os.IBinder
import android.util.Log
import androidx.core.app.NotificationCompat
import com.google.firebase.firestore.DocumentChange
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import java.util.concurrent.TimeUnit

class FirestoreForegroundService : Service() {

    private val channelId = "FirestoreServiceChannel"
    private val tag = "FirestoreService"
    private var lastNotificationTime: Long = 0
    private val notificationInterval: Long = TimeUnit.MINUTES.toMillis(5) // Adjust the interval as needed
    private var isInitialLoad = true
    override fun onCreate() {
        super.onCreate()
        createNotificationChannel()
        startForeground(1, getNotification("Service Running"))
        listenToFirestoreChanges()
    }

    private fun listenToFirestoreChanges() {
        val db: FirebaseFirestore = Firebase.firestore

        db.collection("events")
            .addSnapshotListener { snapshots, e ->

                if (snapshots != null) {
                    if(isInitialLoad){
                        isInitialLoad = false
                        return@addSnapshotListener
                    }
                    for (dc in snapshots.documentChanges) {
                        if (dc.type == DocumentChange.Type.ADDED) {
                            val currentTime = System.currentTimeMillis()
                            if (currentTime - lastNotificationTime > notificationInterval) {
                                lastNotificationTime = currentTime
                                sendNotification("New Event", "A new event has been added.")
                            }
                        }
                    }
                }
            }
    }

    private fun sendNotification(title: String, message: String) {
        val notification: Notification = NotificationCompat.Builder(this, channelId)
            .setContentTitle(title)
            .setContentText(message)
            .setSmallIcon(R.drawable.ic_notifications_black_24dp) // Replace with your icon
            .build()


        val notificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.notify(1, notification)
    }

    private fun getNotification(message: String): Notification {
        return NotificationCompat.Builder(this, channelId)
            .setContentTitle("Firestore Service")
            .setContentText(message)
            .setSmallIcon(R.drawable.ic_notifications_black_24dp) // Replace with your icon
            .build()
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val serviceChannel = NotificationChannel(
                channelId,
                "Firestore Service Channel",
                NotificationManager.IMPORTANCE_DEFAULT
            )
            val manager = getSystemService(NotificationManager::class.java)
            manager?.createNotificationChannel(serviceChannel)
        }
    }

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        return START_STICKY
    }

    override fun onDestroy() {
        super.onDestroy()
    }
}
