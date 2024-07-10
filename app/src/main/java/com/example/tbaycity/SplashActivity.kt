package com.example.tbaycity

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import android.widget.ProgressBar
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class SplashActivity : AppCompatActivity() {
    private lateinit var loading:ProgressBar
    private var progressStatus = 0
    private val handler = Handler(Looper.getMainLooper())

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.splash_activity)
        loading = findViewById<ProgressBar>(R.id.loadingbar)
        loading.visibility = View.VISIBLE
        Thread {
            while (progressStatus < 100) {
                progressStatus += 1
                handler.post {
                    loading.progress = progressStatus
                }
                Thread.sleep(10) // Simulate a time-consuming task
            }
            launch_login()
        }.start()


    }
    private fun launch_login(){
        handler.postDelayed({
            val intent = Intent(this,LoginActivity::class.java)
            loading.visibility = View.GONE
            startActivity(intent)
        }, 100)
    }
}