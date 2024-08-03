package com.example.tbaycity

import android.app.ActivityManager.TaskDescription
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.GridLayout.Spec
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.firestore.FirebaseFirestore
import java.util.Objects


class ServiceRequestFragment : Fragment() {
    private lateinit var requestType:Spinner
    private lateinit var requestService:Spinner
    private lateinit var serviceDescription:EditText
    private lateinit var category:String
    private  lateinit var submitBtn: Button
    private  lateinit var auth:FirebaseAuth
    private  lateinit var firebaseUser: FirebaseUser
    private lateinit var firestore: FirebaseFirestore
    private lateinit var currentUser: String
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_service_request, container, false)
        currentUser = currentUser()
        category = arguments?.getString("category").toString()
        requestType = view.findViewById(R.id.request_type)
        requestService = view.findViewById(R.id.request_service)
        serviceDescription = view.findViewById(R.id.service_description)
        submitBtn = view.findViewById(R.id.submit_request_button)
        // ===== Dynamic spinner code starts from here =====//
        //array of different categories//
        val wasteRequestType = arrayOf("Regular Waste Pickup","Recycling Services","Bulk Item Pickup","Hazardous Waste Disposal"," Street Cleaning")
        val wasteServiceType = arrayOf("Residential Waste Collection","Commercial Waste Collection","Recycling Collection")
        if (category == "Waste") {
            try {
                populateSpinner(category,view,requestType,requestService,wasteRequestType,wasteServiceType)
                }
            catch (e:Exception){
                Log.d("exception",e.toString())
            }
        }
        // ===== Dynamic spinner code ends =====//
        submitBtn.setOnClickListener{
            var selected_request_type:String = requestType.selectedItem.toString()
            var selected_service_type: String = requestService.selectedItem.toString()
            var description = serviceDescription.text.toString()
            if(selected_request_type.isNotBlank() && selected_service_type.isNotBlank() && description.isNotBlank()){
                insertData(selected_request_type,selected_service_type,description)
            }
        }
        return view
    }
    private fun populateSpinner(category:String,view:View,type:Spinner,service:Spinner,request_type:Array<String>,service_type:Array<String>){
        type.adapter = ArrayAdapter(requireContext(),android.R.layout.simple_list_item_1,request_type)
        service.adapter = ArrayAdapter(requireContext(),android.R.layout.simple_list_item_1,service_type)
    }
    private fun currentUser():String{
        auth = FirebaseAuth.getInstance()
        return auth.currentUser?.uid.toString()

    }
    private fun insertData(type:String,service:String,description: String){
        val db = FirebaseFirestore.getInstance()
        val serviceData = HashMap<String,Any>()
        val currentTimeStamp = System.currentTimeMillis()
        val timestamp = java.sql.Timestamp(currentTimeStamp)
        serviceData.put("userId",currentUser())
        serviceData.put("requestType",type)
        serviceData.put("requestService",service)
        serviceData.put("requestDescription",description)
        serviceData.put("requestTime", timestamp)
        var loadingAlert = LoadingAlert(requireActivity())
        loadingAlert.startAlertDialog()
        db.collection("service").document().set(serviceData).addOnSuccessListener {

            Toast.makeText(requireContext(),"Submitted the request",Toast.LENGTH_LONG).show()
            loadingAlert.dismissAlertDialog()
            changeFragment(Service_Fragment())

        }
            .addOnFailureListener{
                Toast.makeText(requireContext(),"Fail  to submit the request",Toast.LENGTH_LONG).show()
                loadingAlert.dismissAlertDialog()
            }
    }
    private fun changeFragment(fragment: Fragment){
        val fragmentManager = parentFragmentManager
        val fragmentTransaction = fragmentManager.beginTransaction()

        fragmentTransaction.replace(R.id.frame_layout,fragment)
        fragmentTransaction.addToBackStack(null)
//        fragmentTransaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
        fragmentTransaction.commit()
    }
}