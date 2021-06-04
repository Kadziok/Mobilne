package com.example.gtamapirl

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.firebase.ui.database.FirebaseRecyclerAdapter
import com.firebase.ui.database.FirebaseRecyclerOptions
import com.example.gtamapirl.databinding.ItemEventBinding

class EventAdapter (private val options: FirebaseRecyclerOptions<EventElement>) :
    FirebaseRecyclerAdapter<EventElement, RecyclerView.ViewHolder>(options) {
    lateinit var context : Context

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val view = inflater.inflate(R.layout.item_event, parent, false)
        val binding = ItemEventBinding.bind(view)
        context = view.context
        return EventViewHolder(binding)
    }

    inner class EventViewHolder(private val binding: ItemEventBinding) : RecyclerView.ViewHolder(binding.root) {

        fun bind(item: EventElement) {
            binding.eventId.text = item.id
            binding.eventStatus.text = item.state
        }

    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int, model: EventElement) {
        (holder as EventViewHolder).bind(model)
    }
}