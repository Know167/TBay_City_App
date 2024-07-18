package com.example.tbaycity

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.PagerSnapHelper
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.auth.FirebaseAuth



// storing events data from the firestore
data class Event(
    val eventImageURL: String? = null,
    val date: com.google.firebase.Timestamp? = null,
    val title: String? = null,
    val description: String? = null,
    val location: String? = null,
    val contactNumber: String? = null,
    val blogURL: String? = null,
    val startTimeEndTime: String? = null,
    val image: Bitmap? = null,
)

class HomeFragment : Fragment() {
    private  lateinit var auth: FirebaseAuth
    private lateinit var viewallservice:TextView
    private lateinit var carouselRecyclerView: RecyclerView
    private lateinit var carouselAdapter: CarouselAdapter
    private val firestore = FirebaseFirestore.getInstance()
    private val carouselItems = mutableListOf<CarouselItem>()


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

        carouselRecyclerView = view.findViewById(R.id.carousel_recycler_view)

        val profileIcon = view.findViewById<ImageView>(R.id.profileIcon)

        profileIcon.setOnClickListener{
//            findNavController().navigate(R.id.navigation_dashboard)
//
//            changeFragment(ProfileFragment())
            auth.signOut()
            activity?.let {  moveToNewActivity(it, LoginActivity::class.java)}


        }
        viewallservice.setOnClickListener{
            val intent = Intent(activity,CityServices::class.java)
            startActivity(intent)
        }