package com.example.tbaycity

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.widget.FrameLayout
import android.widget.ImageButton
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.request.RequestOptions
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentChange
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage

class HomeActivity : AppCompatActivity() {
    private lateinit var bottomNavigationView: BottomNavigationView
    private lateinit var framelayout: FrameLayout
    private lateinit var auth: FirebaseAuth
    private lateinit var db: FirebaseFirestore
    private lateinit var storage: FirebaseStorage
    private lateinit var sharedPreferences: SharedPreferences
    //private lateinit var eventsListener: ListenerRegistration

    val preferenceChangeListener =
        SharedPreferences.OnSharedPreferenceChangeListener { sharedPreferences, key ->
            if (key == "profileImageUrl") {
                val profileIcon = findViewById<ImageButton>(R.id.profileIcon)
                loadProfileImage(profileIcon)
            }
        }

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_home)

//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
//            createNotificationChannel()
//        }

        bottomNavigationView = findViewById(R.id.bottomNavView)
        framelayout = findViewById(R.id.frame_layout)
        if (savedInstanceState == null) {
            changeFragment(HomeFragment())
        }

        auth = FirebaseAuth.getInstance()
        storage = FirebaseStorage.getInstance()
        db = FirebaseFirestore.getInstance()
        sharedPreferences = getSharedPreferences("UserData", MODE_PRIVATE)

        // Register the listener
        sharedPreferences.registerOnSharedPreferenceChangeListener(preferenceChangeListener)

        bottomNavigationView.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.navigation_home -> changeFragment(HomeFragment())
                R.id.navigation_events -> changeFragment(EventFragment())
                R.id.navigation_profile -> changeFragment(ProfileFragment())
                R.id.navigation_services -> changeFragment(Service_Fragment())
            }
            true
        }

        val profileIcon = findViewById<ImageButton>(R.id.profileIcon)
        loadProfileImage(profileIcon)

        profileIcon.setOnClickListener {
            changeFragment(ProfileFragment())
        }

//        // Listen for new documents in the "events" collection
//        eventsListener = db.collection("events")
//            .addSnapshotListener { snapshots, e ->
//                if (e != null) {
//                    Log.w(TAG, "Listen failed.", e)
//                    return@addSnapshotListener
//                }
//
//                for (dc in snapshots!!.documentChanges) {
//                    if (dc.type == DocumentChange.Type.ADDED) {
//                        Log.d(TAG, "New event: ${dc.document.data}")
//                        sendNotification("New Event", "A new event has been added.")
//                    }
//                }
//            }
    }

    override fun onDestroy() {
        super.onDestroy()
        sharedPreferences.unregisterOnSharedPreferenceChangeListener(preferenceChangeListener)

    }

    private fun loadProfileImage(profileIcon: ImageButton) {
        val profileURL = sharedPreferences.getString("profileImageUrl", null)
        if (profileURL != null) {
            Glide.with(this).load(profileURL).into(profileIcon)
        }
    }

    private fun changeFragment(fragment: Fragment) {
        val fragmentManager = supportFragmentManager
        val fragmentTransaction = fragmentManager.beginTransaction()
        fragmentTransaction.replace(R.id.frame_layout, fragment)
        fragmentTransaction.commit()
    }

//    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
//    private fun sendNotification(title: String, message: String) {
//        val builder = NotificationCompat.Builder(this, CHANNEL_ID)
//            .setSmallIcon(R.drawable.ic_notifications_black_24dp)
//            .setContentTitle(title)
//            .setContentText(message)
//            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
//
//        with(NotificationManagerCompat.from(this)) {
//            if (ActivityCompat.checkSelfPermission(
//                    this@HomeActivity,
//                    Manifest.permission.POST_NOTIFICATIONS
//                ) != PackageManager.PERMISSION_GRANTED
//            ) {
//                // Request the missing permissions
//                ActivityCompat.requestPermissions(
//                    this@HomeActivity,
//                    arrayOf(Manifest.permission.POST_NOTIFICATIONS),
//                    NOTIFICATION_PERMISSION_REQUEST_CODE
//                )
//                return
//            }
//            notify(NOTIFICATION_ID, builder.build())
//        }
//    }

//    @RequiresApi(Build.VERSION_CODES.O)
//    private fun createNotificationChannel() {
//        val name = getString(R.string.channel_name)
//        val descriptionText = getString(R.string.channel_description)
//        val importance = NotificationManager.IMPORTANCE_DEFAULT
//        val channel = NotificationChannel(CHANNEL_ID, name, importance).apply {
//            description = descriptionText
//        }
//        // Register the channel with the system
//        val notificationManager: NotificationManager =
//            getSystemService(NOTIFICATION_SERVICE) as NotificationManager
//        notificationManager.createNotificationChannel(channel)
//    }

//    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
//    override fun onRequestPermissionsResult(
//        requestCode: Int,
//        permissions: Array<out String>,
//        grantResults: IntArray
//    ) {
//        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
//        when (requestCode) {
//            NOTIFICATION_PERMISSION_REQUEST_CODE -> {
//                if ((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
//                    // Permission granted, send the notification
//                    sendNotification("New Event", "A new event has been added.")
//                } else {
//                    // Permission denied, show a message to the user
//                    Toast.makeText(this, "Notification permission denied", Toast.LENGTH_SHORT).show()
//                }
//                return
//            }
//        }
//    }

    private fun downloadImage(profileIcon: ImageButton) {
        val userId = auth.currentUser?.uid ?: return
        val storageRef = storage.reference.child("users/$userId/profile.jpg")

        storageRef.downloadUrl.addOnSuccessListener { uri ->
            Glide.with(this)
                .load(uri)
                .into(profileIcon)
        }.addOnFailureListener {
            Toast.makeText(this, "Failed to load profile image", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onBackPressed() {
        val fragmentManager = supportFragmentManager
        if (fragmentManager.backStackEntryCount > 0) {
            fragmentManager.popBackStack()
        } else {
            super.onBackPressed()
        }
    }

    fun getUserData(auth: FirebaseAuth, firestore: FirebaseFirestore, callback: (User?) -> Unit) {
        val uid = auth.currentUser?.uid
        if (!uid.isNullOrBlank()) {
            firestore.collection("Users").document(uid).get().addOnSuccessListener { document ->
                if (document != null && document.exists()) {
                    val userName = document.getString("userName")
                    val userEmail = document.getString("userEmail")
                    val userProfileURL = document.getString("profileImageUrl")
                    val user = User(uid, userName ?: "", userEmail ?: "", userProfileURL ?: "")
                    callback(user)
                } else {
                    callback(null)
                }
            }
        }
    }

    private fun getUserImage(profileIcon: ImageButton) {
        val profileImageUrl = sharedPreferences.getString("profileImageUrl", null)
        profileImageUrl?.let {
            Glide.with(this)
                .load(it)
                .apply(RequestOptions().transform(RoundedCorners(16)))
                .into(profileIcon)
        } ?: run {
            profileIcon.setImageResource(R.drawable.profile_image_container)
        }
    }

    companion object {
        private const val NOTIFICATION_PERMISSION_REQUEST_CODE = 101
        private const val TAG = "HomeActivity"
        private const val CHANNEL_ID = "events_channel"
        private const val NOTIFICATION_ID = 1
    }
}
