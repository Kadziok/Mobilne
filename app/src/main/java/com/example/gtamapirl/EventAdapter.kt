package com.example.gtamapirl

import android.app.Application
import android.content.Context
import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.firebase.ui.database.FirebaseRecyclerAdapter
import com.firebase.ui.database.FirebaseRecyclerOptions
import com.example.gtamapirl.databinding.EventElementBinding

class EventAdapter (private val options: FirebaseRecyclerOptions<EventElement>) :
    FirebaseRecyclerAdapter<EventElement, RecyclerView.ViewHolder>(options) {
    lateinit var context : Context

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val view = inflater.inflate(R.layout.event_element, parent, false)
        val binding = EventElementBinding.bind(view)
        context = view.context
        return EventViewHolder(binding)
    }

    inner class EventViewHolder(private val binding: EventElementBinding) : RecyclerView.ViewHolder(binding.root) {

        fun bind(item: EventElement) {
            binding.eventId.text = item.id
            binding.eventStatus.text = item.state
        }

    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int, model: EventElement) {
        (holder as EventViewHolder).bind(model)
    }

}