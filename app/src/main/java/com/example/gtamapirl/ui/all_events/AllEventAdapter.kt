package com.example.gtamapirl.ui.all_events

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.gtamapirl.data.EventData
import com.example.gtamapirl.R
import com.example.gtamapirl.databinding.ItemEventBinding
import com.firebase.ui.database.FirebaseRecyclerAdapter
import com.firebase.ui.database.FirebaseRecyclerOptions


class AllEventAdapter(private val options: FirebaseRecyclerOptions<EventData>, val fragment : AllEventsListFragment) :
    FirebaseRecyclerAdapter<EventData, RecyclerView.ViewHolder>(options) {
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

        fun bind(item: EventData) {
            binding.eventName.text = item.name
            binding.eventDate.text = item.date
            binding.eventStatus.text = item.time

            binding.eventParticipants.text =
                    item.participants.toString() +
                    (if (item.participants!! == 1.toLong()) context.getString(R.string.event_participant) else context.getString(R.string.event_participant_plural))

            binding.eventLay.setOnClickListener {
                fragment.goToEvent(item.id!!)
            }
        }

    }

    override fun onBindViewHolder(
        holder: RecyclerView.ViewHolder,
        position: Int,
        model: EventData
    ) {
        (holder as EventViewHolder).bind(model)
    }

}