package com.example.tbaycity

import android.R.attr.name
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.FrameLayout
import android.widget.ImageButton
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage


class HomeActivity : AppCompatActivity() {
    private lateinit var bottomNavigationView:BottomNavigationView
    private lateinit var framelayout:FrameLayout
    private  lateinit var auth: FirebaseAuth
    private lateinit var db: FirebaseFirestore
    private lateinit var storage: FirebaseStorage
    private val firestore = FirebaseFirestore.getInstance()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_home)

        bottomNavigationView = findViewById(R.id.bottomNavView)
        framelayout = findViewById(R.id.frame_layout)
        if (savedInstanceState == null) {
            changeFragment(HomeFragment())
        }

        auth = FirebaseAuth.getInstance()
        storage = FirebaseStorage.getInstance()
        getUserData(auth,firestore){
            user -> user?.let {

            val sharedPreferences = getSharedPreferences("UserData", MODE_PRIVATE)

            val myEdit = sharedPreferences.edit()
            Log.d("name",it.name)
            myEdit.putString("name",it.name)
            myEdit.putString("email",it.email)
            myEdit.commit()
        }
        }
        bottomNavigationView.setOnItemSelectedListener {item->
            when(item.itemId){
                R.id.navigation_home -> changeFragment(HomeFragment())
                R.id.navigation_events -> changeFragment(EventFragment())
                R.id.navigation_profile -> changeFragment(ProfileFragment())
                R.id.navigation_services -> changeFragment(Service_Fragment())
        }
            true

        }
        val profileIcon = findViewById<ImageButton>(R.id.profileIcon)
        downloadImage(profileIcon)
        profileIcon.setOnClickListener{
            auth.signOut()
            User("","","")
            val i = Intent(this,LoginActivity::class.java)
            startActivity(i)
            finish()

        }
    }
    private fun changeFragment(fragment: Fragment){
        val fragmentManager = supportFragmentManager
        val fragmentTransaction = fragmentManager.beginTransaction()
        fragmentTransaction.replace(R.id.frame_layout,fragment)
        fragmentTransaction.commit()
    }
    private fun downloadImage(profileIcon:ImageButton){
        val userId = auth.currentUser?.uid?:return
        val storageRef = storage.reference.child("users/$userId/profile.jpg")

        storageRef.downloadUrl.addOnSuccessListener { uri ->
            Glide.with(this)
                .load(uri)
                .into(profileIcon);
        }.addOnFailureListener {
            Toast.makeText(this, "Failed to load profile image", Toast.LENGTH_SHORT).show()
        }
    }
    override fun onBackPressed() {
        val fragmentManager = supportFragmentManager
        if (fragmentManager.backStackEntryCount > 0) {
            // If there are fragments in the back stack, pop the back stack
            fragmentManager.popBackStack()
        } else {
            // If no fragments in the back stack, proceed with the default back press action
            super.onBackPressed()
        }
    }
    fun getUserData(auth: FirebaseAuth,firestore: FirebaseFirestore,callback:(com.example.tbaycity.User?)->Unit){
        val uid = auth.currentUser?.uid
        val db = Firebase.firestore
        if(uid.toString().isNotBlank()){
            if (uid != null) {
                db.collection("Users").document(uid).get().addOnSuccessListener{ document->
                    if(document!=null && document.exists()){
                        val userName = document.getString("userName")
                        val userEmail = document.getString("userEmail")
                        val user = User(uid.toString(),userName.toString(),userEmail.toString())
                        callback(user)
                    }
                    else{
                        callback(null)
                    }

                }
            }
        }
    }
}