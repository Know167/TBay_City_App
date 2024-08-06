package com.example.tbaycity

import android.app.ActivityManager.TaskDescription
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView


class EventDetailFragment : Fragment() {
   private lateinit var eventnewsTitle:TextView
   private lateinit var eventnewsDate:TextView
   private lateinit var eventnewsAddress:TextView
   private lateinit var eventnewsImage:ImageView
   private lateinit var eventnewsDescriptionTitle:TextView
   private lateinit var eventnewsDescription:TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_event_detail, container, false)
        eventnewsTitle = view.findViewById(R.id.eventnews_title)
        eventnewsDate = view.findViewById(R.id.eventnews_date)
        eventnewsImage = view.findViewById(R.id.eventnews_image)
        eventnewsDescription = view.findViewById(R.id.eventnews_description)
        eventnewsAddress = view.findViewById(R.id.eventnews_address)
        eventnewsDescriptionTitle = view.findViewById(R.id.eventnews_description_title)

        return  view
    }


}