package com.example.gtamapirl.ui.my_events

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.gtamapirl.R
import com.example.gtamapirl.data.UserEventData
import com.example.gtamapirl.databinding.ItemEventBinding
import com.firebase.ui.database.FirebaseRecyclerAdapter
import com.firebase.ui.database.FirebaseRecyclerOptions
import com.google.firebase.database.FirebaseDatabase


class EventAdapter(private val options: FirebaseRecyclerOptions<UserEventData>, val fragment : EventsListFragment) :
    FirebaseRecyclerAdapter<UserEventData, RecyclerView.ViewHolder>(options) {
    lateinit var context : Context

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val view = inflater.inflate(R.layout.item_event, parent, false)

        val binding = ItemEventBinding.bind(view)
        context = view.context
        return EventViewHolder(binding)
    }

    inner class EventViewHolder(private val binding: ItemEventBinding) : RecyclerView.ViewHolder(
        binding.root
    ) {

        fun bind(item: UserEventData) {
            val db = FirebaseDatabase.getInstance().reference

            db.child("events").child(item.id!!).child("name").get().addOnSuccessListener {
                binding.eventName.text = it.value.toString()
            }.addOnFailureListener{
                Log.e("firebase", "Error getting data", it)
            }

            db.child("events").child(item.id!!).child("date").get().addOnSuccessListener {
                binding.eventDate.text = it.value.toString()
            }.addOnFailureListener{
                Log.e("firebase", "Error getting data", it)
            }

            binding.eventStatus.text = item.state

            binding.eventLay.setOnClickListener {
                fragment.goToEvent(item.id!!)
            }

        }

    }

    override fun onBindViewHolder(
        holder: RecyclerView.ViewHolder,
        position: Int,
        model: UserEventData
    ) {
        (holder as EventViewHolder).bind(model)
    }
}