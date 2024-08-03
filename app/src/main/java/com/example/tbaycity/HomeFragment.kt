package com.example.tbaycity

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.PagerSnapHelper
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.storage.FirebaseStorage

class HomeFragment : Fragment() {
//    private  lateinit var auth: FirebaseAuth
//    private lateinit var db: FirebaseFirestore
//    private lateinit var storage: FirebaseStorage
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

        //auth = FirebaseAuth.getInstance()

        val  view:View = inflater.inflate(R.layout.fragment_home, container, false)
        viewallservice = view.findViewById(R.id.viewAllService)

        carouselRecyclerView = view.findViewById(R.id.carousel_recycler_view)

//        val profileIcon = view.findViewById<ImageButton>(R.id.profileIcon)
//        downloadImage(profileIcon)
//        profileIcon.setOnClickListener{
////            findNavController().navigate(R.id.navigation_dashboard)
////
////            changeFragment(ProfileFragment())
//            auth.signOut()
//            activity?.let {  moveToNewActivity(it, LoginActivity::class.java)}
//
//
//        }
        viewallservice.setOnClickListener{
            val intent = Intent(activity,CityServices::class.java)
            startActivity(intent)
        }

        carouselRecyclerView.layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
        carouselAdapter = CarouselAdapter(carouselItems)
        carouselRecyclerView.adapter = carouselAdapter

        val snapHelper = PagerSnapHelper()
        snapHelper.attachToRecyclerView(carouselRecyclerView)
        fetchCarouselItems()

        return view
    }
    private fun fetchCarouselItems(){
        firestore.collection("events")
            .orderBy("date", com.google.firebase.firestore.Query.Direction.DESCENDING)
            .limit(5)
            .get()
            .addOnSuccessListener { documents ->
                for (document in documents) {
                    val item = CarouselItem(title=document.data["title"].toString(),
                        description = document.data["description"].toString(),
                        imgUrl = document.data["eventImageURL"].toString(),
                        date = document.data["date"] as com.google.firebase.Timestamp,
                        location = document.data["location"].toString(),
                        contactNumber = document.data["contactNumber"].toString(),
                        blogURL = document.data["blogURL"].toString(),
                        startTimeEndTime = document.data["startTimeEndTime"].toString())
                    carouselItems.add(item)
                }
                carouselAdapter.notifyDataSetChanged()
            }
            .addOnFailureListener { exception ->
                Toast.makeText(context,"Couldn't fetch event data",Toast.LENGTH_SHORT).show()

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
//    private fun downloadImage(profileIcon:ImageButton){
//        val userId = auth.currentUser?.uid?:return
//        val storageRef = storage.reference.child("users/$userId/profile.jpg")
//
//        storageRef.downloadUrl.addOnSuccessListener { uri ->
//            Glide.with(this)
//                .load(uri)
//                .into(profileIcon);
//        }.addOnFailureListener {
//            Toast.makeText(context, "Failed to load profile image", Toast.LENGTH_SHORT).show()
//        }
//    }
}