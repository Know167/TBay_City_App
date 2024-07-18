package com.example.tbaycity

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage


class HomeFragment : Fragment() {
    private  lateinit var auth: FirebaseAuth
    private  lateinit var  firebaseStorage: FirebaseStorage
    private  lateinit var  firebaseFirestore: FirebaseFirestore
    private  lateinit var  firebaseUser: FirebaseUser
    private lateinit var viewallservice:TextView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        auth = FirebaseAuth.getInstance()
        val  view:View = inflater.inflate(R.layout.fragment_home, container, false)
        viewallservice = view.findViewById(R.id.viewAllService)
        val profileIcon = view.findViewById<ImageView>(R.id.profileIcon)

        profileIcon.setOnClickListener{
//            findNavController().navigate(R.id.navigation_dashboard)

//            changeFragment(ProfileFragment())
            auth.signOut()
            activity?.let {  moveToNewActivity(it, LoginActivity::class.java)}


        }
        viewallservice.setOnClickListener{
            val intent = Intent(activity,CityServices::class.java)
            startActivity(intent)
        }
        return view
    }
    private fun changeFragment(fragment: Fragment){
        val fragmentManager = parentFragmentManager
        val fragmentTransaction = fragmentManager.beginTransaction()
        fragmentTransaction.replace(R.id.frame_layout,fragment)
        fragmentTransaction.commit()
    }
    private fun moveToNewActivity(currentActivity: Activity, targetActivity: Class<out Activity>) {
        val intent = Intent(currentActivity, targetActivity)
        startActivity(intent)
    }
}