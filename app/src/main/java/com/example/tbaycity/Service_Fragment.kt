package com.example.tbaycity

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.fragment.app.FragmentTransaction
import org.w3c.dom.Text

class Service_Fragment: Fragment() ,View.OnClickListener {
   private lateinit var trashIcon:CardView
    private lateinit var roadIcon:CardView
    private lateinit var electrictyIcon:CardView
    private lateinit var waterIcon:CardView
    private  lateinit var gasIcon: CardView
    private lateinit var neighbourIcon: CardView
    private lateinit var trashText:TextView
    private lateinit var roadText:TextView
    private lateinit var electrictyText:TextView
    private lateinit var waterText:TextView


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        
        val view = inflater.inflate(R.layout.fragment_service_, container, false)
        trashIcon = view.findViewById(R.id.trash_icon)
        roadIcon = view.findViewById(R.id.road_icon)
        electrictyIcon = view.findViewById(R.id.electricity_icon)
        waterIcon = view.findViewById(R.id.water_icon)
        gasIcon=view.findViewById(R.id.gas_icon)
        neighbourIcon=view.findViewById(R.id.neighbour_icon)
        trashText = view.findViewById(R.id.trash_text)
        roadText = view.findViewById(R.id.road_text)
        electrictyText = view.findViewById(R.id.electricity_text)
        waterText = view.findViewById(R.id.water_text)
        trashIcon.setOnClickListener(this)
        roadIcon.setOnClickListener(this)
        electrictyIcon.setOnClickListener(this)
        waterIcon.setOnClickListener(this)
        gasIcon.setOnClickListener(this)
        neighbourIcon.setOnClickListener(this)
        return view
    }

    override fun onClick(v: View?) {
        val bundle = Bundle()
        var category = ""
        val inputString = "category"

        when(v!!.id){
            R.id.trash_icon ->{
                category = "Waste"
                bundle.putString(inputString,category)
                changeFragment(ServiceRequestFragment(),bundle)
            }
            R.id.road_icon ->{
                category = "Road"
                bundle.putString(inputString,category)
                changeFragment(ServiceRequestFragment(),bundle)
            }
            R.id.electricity_icon ->{
                category = "Electricity"
                bundle.putString(inputString,category)
                changeFragment(ServiceRequestFragment(),bundle)
            }
            R.id.water_icon ->{
                category = "Water"
                bundle.putString(inputString,category)
                changeFragment(ServiceRequestFragment(),bundle)
            }
            R.id.gas_icon ->{
                category = "Gas"
                bundle.putString(inputString,category)
                changeFragment(ServiceRequestFragment(),bundle)
            }
            R.id.neighbour_icon ->{
                category = "Neighbour"
                bundle.putString(inputString,category)
                changeFragment(ServiceRequestFragment(),bundle)
            }
        }
    }
    private fun changeFragment(fragment: Fragment,bundle:Bundle){
        val fragmentManager = parentFragmentManager
        val fragmentTransaction = fragmentManager.beginTransaction()
        fragment.arguments=bundle
        fragmentTransaction.replace(R.id.frame_layout,fragment)
        fragmentTransaction.addToBackStack(null)
//        fragmentTransaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
        fragmentTransaction.commit()
    }

}