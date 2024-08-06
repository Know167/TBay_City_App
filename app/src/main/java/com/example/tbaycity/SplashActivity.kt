package com.example.tbaycity

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import android.widget.ProgressBar
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

import com.google.firebase.auth.FirebaseAuth

class SplashActivity : AppCompatActivity() {

    private lateinit var loading: ProgressBar
    private var progressStatus = 0
    private val handler = Handler(Looper.getMainLooper())
    private lateinit var auth: FirebaseAuth


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.splash_activity)
        val serviceIntent = Intent(this, FirestoreForegroundService::class.java)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            startForegroundService(serviceIntent)
        } else {
            startService(serviceIntent)
        }
//        loading = findViewById(R.id.loadingbar)
//        loading.visibility = View.VISIBLE
        auth = FirebaseAuth.getInstance()

        Thread {
//            while (progressStatus < 100) {
//                progressStatus += 1
//                handler.post {
//                    loading.progress = progressStatus
//                }
//                Thread.sleep(10) // Simulate a time-consuming task
//            }

            checkUserAuthentication()
        }.start()
    }

    private fun checkUserAuthentication() {
        val currentUser = auth.currentUser
        if (currentUser != null) {
            // User is signed in, navigate to HomeActivity
            navigateToHome()
        } else {
            // User is not signed in, navigate to LoginActivity
            launchLogin()
        }
    }

    private fun launchLogin() {
        handler.postDelayed({
            val intent = Intent(this, LoginActivity::class.java)
//            loading.visibility = View.GONE
            startActivity(intent)
            finish() // Optional: Close current activity to prevent going back
        }, 1000)
    }

    private fun navigateToHome() {
        handler.postDelayed({
            val intent = Intent(this, HomeActivity::class.java)
//            loading.visibility = View.GONE
            startActivity(intent)
            finish() // Optional: Close current activity to prevent going back
        }, 100)
    }
}

