package com.example.tbaycity

import android.os.Bundle
import android.widget.GridView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.tbaycity.ui.ServiceAdapter
import com.example.tbaycity.ui.ServiceModel

class CityServices : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_city_services)
//        val gridServices = findViewById<GridView>(R.id.serviceGrid)
//        val serviceArrayList : ArrayList<ServiceModel> = ArrayList<ServiceModel>()
//        serviceArrayList.add(ServiceModel("Trash Management",R.drawable.trashicon))
//        serviceArrayList.add(ServiceModel("Road Problems",R.drawable.constructionicon))
//
//        val adapter = ServiceAdapter(this,serviceArrayList)
//        gridServices.adapter = adapter
    }
}