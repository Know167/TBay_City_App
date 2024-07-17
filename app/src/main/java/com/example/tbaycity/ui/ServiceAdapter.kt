package com.example.tbaycity.ui

import android.app.Service
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.TextView
import com.example.tbaycity.R

class ServiceAdapter(context: Context, serviceModelArrayList: ArrayList<ServiceModel?>?):
    ArrayAdapter<ServiceModel?>(context, 0, serviceModelArrayList!!) {
    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {

        var listitemView = convertView
        if (listitemView == null) {
            // Layout Inflater inflates each item to be displayed in GridView.
            listitemView = LayoutInflater.from(context).inflate(R.layout.card_item, parent, false)
        }

        val serviceModel: ServiceModel? = getItem(position)
        val serviceText = listitemView!!.findViewById<TextView>(R.id.serviceIdText)
        val serviceImage = listitemView.findViewById<ImageView>(R.id.serviceIdImg)

        if (serviceModel != null) {
            serviceText.setText(serviceModel.getservicename())
        }
        if (serviceModel != null) {
            serviceImage.setImageResource(serviceModel.getimgid())
        }
        return listitemView
    }
}