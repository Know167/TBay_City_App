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
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.PagerSnapHelper
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
    private lateinit var carouselRecyclerView: RecyclerView
    private lateinit var carouselAdapter: CarouselAdapter
    private val firestore = FirebaseFirestore.getInstance()
    private val carouselItems = mutableListOf<CarouselItem>()


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
        carouselRecyclerView = view.findViewById(R.id.carousel_recycler_view)
//

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
            .limit(5)
            .get()
            .addOnSuccessListener { documents ->
                for (document in documents) {
                    val title=document.getString("title")
                    val description=document.getString("description")
                    val imgUrl=document.getString("eventImageURL")

                    val item = CarouselItem(title!!,description!!,imgUrl!!)
                    carouselItems.add(item)
                }
                carouselAdapter.notifyDataSetChanged()
            }
            .addOnFailureListener { exception ->
                Toast.makeText(context,"Couldn't fetch event data",Toast.LENGTH_SHORT).show()
                val title="couldn't load events"
                val description=" "
                val imgUrl="https://placehold.co/100x80/png?text=Couldnt+Load+Events"

                val item = CarouselItem(title!!,description!!,imgUrl!!)
                carouselItems.add(item)
                carouselAdapter.notifyDataSetChanged()

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