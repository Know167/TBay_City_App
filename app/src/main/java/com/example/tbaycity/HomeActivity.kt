package com.example.tbaycity

import android.os.Bundle
import android.widget.FrameLayout
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.fragment.app.Fragment
import com.google.android.material.bottomnavigation.BottomNavigationMenuView
import com.google.android.material.bottomnavigation.BottomNavigationView

class HomeActivity : AppCompatActivity() {
    private lateinit var bottomNavigationView:BottomNavigationView
    private lateinit var framelayout:FrameLayout
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_home)
        bottomNavigationView = findViewById(R.id.bottomNavView)
        framelayout = findViewById(R.id.frame_layout)
        bottomNavigationView.setOnItemSelectedListener {item->
            when(item.itemId){
                R.id.navigation_home -> changeFragment(HomeFragment())
                R.id.navigation_notifications -> changeFragment(NotificationFragment())
                R.id.navigation_dashboard -> changeFragment(ProfileFragment())

        }
            true

        }
    }
    private fun changeFragment(fragment: Fragment){
        val fragmentManager = supportFragmentManager
        val fragmentTransaction = fragmentManager.beginTransaction()
        fragmentTransaction.replace(R.id.frame_layout,fragment)
        fragmentTransaction.commit()
    }
}