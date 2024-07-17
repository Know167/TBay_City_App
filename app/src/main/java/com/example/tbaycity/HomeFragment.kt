package com.example.tbaycity

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView


class HomeFragment : Fragment() {

    private lateinit var viewallservice:TextView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val  view:View = inflater.inflate(R.layout.fragment_home, container, false)
        viewallservice = view.findViewById(R.id.viewAllService)
        viewallservice.setOnClickListener{
            val intent = Intent(activity,CityServices::class.java)
            startActivity(intent)
        }
        return view
    }
}