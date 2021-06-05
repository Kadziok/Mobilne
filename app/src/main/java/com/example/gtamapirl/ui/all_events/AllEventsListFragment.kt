package com.example.gtamapirl.ui.all_events

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.gtamapirl.all_events.AllEventsListFragmentDirections
import com.example.gtamapirl.data.EventData
import com.example.gtamapirl.R
import com.firebase.ui.database.FirebaseRecyclerOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase

class AllEventsListFragment : Fragment() {
    private lateinit var cUser: FirebaseUser
    private lateinit var adapter: AllEventAdapter

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        cUser = FirebaseAuth.getInstance().currentUser!!
        val db = Firebase.database

        val eventsRef = db.reference.child("events")
        val options = FirebaseRecyclerOptions.Builder<EventData>()
            .setQuery(eventsRef, EventData::class.java)
            .build()

        adapter = AllEventAdapter(options, this)
        adapter.startListening()

        val manager = LinearLayoutManager(view.context)
        val recycler = view.findViewById<RecyclerView>(R.id.eventRecyclerView)

        recycler.layoutManager = manager
        recycler.adapter = adapter
    }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_events_list, container, false)
    }

    override fun onPause() {
        adapter.stopListening()
        super.onPause()
    }

    override fun onResume() {
        super.onResume()
        adapter.startListening()
    }

    fun goToEvent(id: String) {
        val action = AllEventsListFragmentDirections.actionGoToEvent(id)
        findNavController().navigate(action)
    }

}