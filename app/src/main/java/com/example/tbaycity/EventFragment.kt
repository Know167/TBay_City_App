package com.example.tbaycity

import android.graphics.Color
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.firestore.FirebaseFirestore
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import kotlin.collections.ArrayList

class EventFragment : Fragment() {
    private lateinit var eventBtn: Button
    private lateinit var newsBtn: Button
    private lateinit var recyclerView: RecyclerView
    private lateinit var eventList: ArrayList<Events>
    private lateinit var newsList: ArrayList<News>
    private lateinit var eventAdapter: EventAdapter
    private lateinit var newsAdapter: NewsAdapter
    private lateinit var db: FirebaseFirestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_event, container, false)
        eventBtn = view.findViewById(R.id.event_btn)
        newsBtn = view.findViewById(R.id.news_btn)
        recyclerView = view.findViewById(R.id.event_recycler_view)
        recyclerView.layoutManager = LinearLayoutManager(context)
        createEventRecyclerView()
        val bgselected = ContextCompat.getDrawable(requireContext(),R.drawable.tab_selected_background)
        val bgUnselected = ContextCompat.getDrawable(requireContext(),R.drawable.tab_unselected_background)
        val txtColor1 = "white"
        val bgColor2 = "#d9d9d9"
        val txtColor2 = "black"
        eventBtn.setOnClickListener {
            eventBtn.setBackground(bgselected)
            eventBtn.setTextColor(Color.parseColor(txtColor1))
            newsBtn.setBackground(bgUnselected)
            newsBtn.setTextColor(Color.parseColor(txtColor2))
            createEventRecyclerView()
        }
        newsBtn.setOnClickListener {
            newsBtn.setBackground(bgselected)
            newsBtn.setTextColor(Color.parseColor(txtColor1))
            eventBtn.setBackground(bgUnselected)
            eventBtn.setTextColor(Color.parseColor(txtColor2))
            createNewsRecyclerView()
        }
        return view
    }

    // Function to parse date string to Date object
    private fun parseDateString(dateStr: String): Date? {
        val dateFormat = SimpleDateFormat("MMMM d, yyyy hh:mm a", Locale.ENGLISH)
        val regex = Regex("Posted on .*?, (.*) (\\d+), (\\d+) (\\d+:\\d+ [APM]{2})")
        val match = regex.find(dateStr)

        return if (match != null) {
            val (month, day, year, time) = match.destructured
            val dateString = "$month $day, $year $time"
            dateFormat.parse(dateString)
        } else {
            null
        }
    }

    private fun createEventRecyclerView() {
        eventList = arrayListOf()
        eventAdapter = EventAdapter(eventList)
        recyclerView.adapter = eventAdapter
        attachEventData()
    }

    private fun createNewsRecyclerView() {
        newsList = arrayListOf()
        newsAdapter = NewsAdapter(newsList)
        recyclerView.adapter = newsAdapter
        attachNewsData()
    }

    private fun attachNewsData() {
        db = FirebaseFirestore.getInstance()
        db.collection("news")
            .orderBy("datetime",com.google.firebase.firestore.Query.Direction.DESCENDING)
            .limit(10)
            .get()
            .addOnSuccessListener { documents ->
                val newsItems = ArrayList<News>()

                for (document in documents) {
                    val data = document.data
                    val dateStr = data["datetime"] as? String
                    val parsedDate = dateStr?.let { parseDateString(it) }

                    val item = News(
                        title = data["title"].toString(),
                        eventImageLink = data["eventImageLink"].toString(),
                        link = data["link"].toString(),
                        dateTime = parsedDate.toString() // Update with Date object
                    )
                    if (parsedDate != null) {
                        newsItems.add(item)
                    }
                }

                // Sort by parsedDate in descending order and get the top 5
                val topNewsItems = newsItems.sortedByDescending { it.dateTime }
                    .take(5)

                newsList.clear()
                newsList.addAll(topNewsItems)
                newsAdapter.notifyDataSetChanged()
            }
            .addOnFailureListener { exception ->
                Log.e("Firestore Error", "Error fetching news: $exception")
            }
    }

    private fun attachEventData() {
        db = FirebaseFirestore.getInstance()
        db.collection("events")
            .orderBy("date", com.google.firebase.firestore.Query.Direction.DESCENDING)
            .limit(5)
            .get()
            .addOnSuccessListener { documents ->
                for (document in documents) {
                    val item = Events(
                        title = document.data["title"].toString(),
                        description = document.data["description"].toString(),
                        eventImageURL = document.data["eventImageURL"].toString(),
                        date = (document.data["date"] as com.google.firebase.Timestamp),
                        location = document.data["location"].toString(),
                        contactNumber = document.data["contactNumber"].toString(),
                        blogURL = document.data["blogURL"].toString(),
                        startTimeEndTime = document.data["startTimeEndTime"].toString()
                    )
                    eventList.add(item)
                }
                eventAdapter.notifyDataSetChanged()
            }
    }
}
