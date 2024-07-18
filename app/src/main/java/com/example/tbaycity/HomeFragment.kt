package com.example.tbaycity

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import com.google.firebase.firestore.FirebaseFirestore
import java.util.concurrent.Executors
import android.os.Handler
import android.os.Looper
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.storage.FirebaseStorage


class HomeFragment : Fragment() {
    private  lateinit var auth: FirebaseAuth
    private  lateinit var  firebaseStorage: FirebaseStorage
    private  lateinit var  firebaseFirestore: FirebaseFirestore
    private  lateinit var  firebaseUser: FirebaseUser
    private lateinit var viewallservice:TextView
    private lateinit var firestore: FirebaseFirestore
    private lateinit var eventImage: ImageView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        firestore = FirebaseFirestore.getInstance()

    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        auth = FirebaseAuth.getInstance()
        val  view:View = inflater.inflate(R.layout.fragment_home, container, false)
        viewallservice = view.findViewById(R.id.viewAllService)
        eventImage = view.findViewById(R.id.eventImage)

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
        fetchDataFromFirestore()
        return view
    }

    private fun fetchDataFromFirestore() {
        // Replace "yourCollection" with the actual collection name you want to fetch data from

        firestore.collection("events")
            .get()
            .addOnSuccessListener { documents ->
                for (document in documents) {
                    Log.d("HomeFragment", "${document.id} => ${document.data["eventImageURL"]}")
                    val imageURL = document.data["eventImageURL"].toString()
                    // Update your UI with the data here
                    val executor = Executors.newSingleThreadExecutor()
                    val handler = Handler(Looper.getMainLooper())

                    // Initializing the image
                    var image: Bitmap? = null
                    executor.execute {
                        try {
                            val `in` = java.net.URL(imageURL).openStream()
                            image = BitmapFactory.decodeStream(`in`)

                            // Only for making changes in UI
                            handler.post {
                                eventImage.setImageBitmap(image)
                            }
                        }
                        catch (e: Exception) {
                            e.printStackTrace()
                        }
                    }
                }
            }
            .addOnFailureListener { exception ->
                Log.w("HomeFragment", "Error getting documents: ", exception)
            }
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