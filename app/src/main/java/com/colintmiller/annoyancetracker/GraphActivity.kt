package com.colintmiller.annoyancetracker

import android.app.PendingIntent.getActivity
import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v4.content.ContextCompat.startActivity
import android.support.v7.util.SortedList
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.*
import com.jjoe64.graphview.helper.DateAsXAxisLabelFormatter
import com.jjoe64.graphview.series.DataPoint
import com.jjoe64.graphview.series.LineGraphSeries
import kotlinx.android.synthetic.main.activity_graph.*
import java.util.*

class GraphActivity : AppCompatActivity() {

    val dataMap : HashMap<String, Annoyance> = HashMap()
    lateinit var annoyances : DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_graph)

        val user = FirebaseAuth.getInstance().currentUser ?: return

        val database = FirebaseDatabase.getInstance().reference.child("users").child(user.uid)
        annoyances = database.child("annoyances")
        //annoyances.addChildEventListener(childEventListener)
    }

    val childEventListener = object : ChildEventListener {
        override fun onCancelled(p0: DatabaseError) {

        }

        override fun onChildMoved(p0: DataSnapshot, p1: String?) {

        }

        override fun onChildChanged(p0: DataSnapshot, p1: String?) {
            onChildAdded(p0, p1)
        }

        override fun onChildAdded(databaseSnapshot: DataSnapshot, p1: String?) {
            val annoyance = databaseSnapshot.getValue(Annoyance::class.java)
            val key = databaseSnapshot.key
            if (key != null && annoyance != null) {
                dataMap.put(key, annoyance)
                updateChart()
            }
        }

        override fun onChildRemoved(p0: DataSnapshot) {
            val key = p0.key
            if (key != null) {
                dataMap.remove(key)
                updateChart()
            }
        }
    }

    override fun onResume() {
        super.onResume()
        if (::annoyances.isInitialized) {
            annoyances.addChildEventListener(childEventListener)
        }
    }

    override fun onPause() {
        if (::annoyances.isInitialized) {
            annoyances.removeEventListener(childEventListener)
        }
        super.onPause()
    }

    fun updateChart() {
        val data = LineGraphSeries<DataPoint>()
        val list = dataMap.values
        val orderedList = list.sorted()
        var firstDate : Date? = null
        var lastDate : Date? = null
        for (value in orderedList) {
            val date = Date(value.created)
            if (firstDate == null) {
                firstDate = date
            }
            lastDate = date
            data.appendData(DataPoint(date, value.level.toDouble()), false, 1000)
        }
        graph.removeAllSeries()
        graph.addSeries(data)

        graph.gridLabelRenderer.labelFormatter = DateAsXAxisLabelFormatter(this)
        graph.gridLabelRenderer.numHorizontalLabels = 3

        if (firstDate != null && lastDate != null) {
            graph.viewport.setMinX(firstDate.time.toDouble())
            graph.viewport.setMaxX(lastDate.time.toDouble())
            graph.viewport.isXAxisBoundsManual = true
        }

        graph.gridLabelRenderer.setHumanRounding(false)
    }
}
