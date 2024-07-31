package com.example.tbaycity

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.cardview.widget.CardView
import org.w3c.dom.Text

class Service_Fragment : Fragment() {
   private lateinit var trashIcon:CardView
    private lateinit var roadIcon:CardView
    private lateinit var electrictyIcon:CardView
    private lateinit var hydroIcon:CardView
    private lateinit var trashText:TextView
    private lateinit var roadText:TextView
    private lateinit var electrictyText:TextView
    private lateinit var hydroText:TextView


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        
        return inflater.inflate(R.layout.fragment_service_, container, false)
    }


}