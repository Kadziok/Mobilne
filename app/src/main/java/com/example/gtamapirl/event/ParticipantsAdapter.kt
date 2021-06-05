package com.example.gtamapirl.event

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.example.gtamapirl.R
import com.example.gtamapirl.data.ParticipantData
import java.util.*
import kotlin.collections.ArrayList

class ParticipantsAdapter (private val data: ArrayList<ParticipantData>)
    : RecyclerView.Adapter<ParticipantsAdapter.ViewHolder>() {

    class ViewHolder(view: View, parent : ParticipantsAdapter) : RecyclerView.ViewHolder(view) {
        val name: TextView
        val state : TextView
        var id : String

        init {
            name = view.findViewById(R.id.name)
            state = view.findViewById(R.id.status)
            id = ""
            view.setOnClickListener {
                Toast.makeText(view.context, id, Toast.LENGTH_LONG).show()
            }


        }

        private fun showDetails(parent : ParticipantsAdapter, text : String) {
            //parent.parent.details(0, Date(), 0, text)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_participant, parent, false)
        return ViewHolder(view, this)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        when (data[position].state) {
            "host" -> holder.state.text = "Host"
            "attends" -> holder.state.text = "Will attend"
            "interested" -> holder.state.text = "Interested"
            else -> holder.state.text = ""
        }
        holder.name.text = data[position].name
        holder.id = data[position].id!!

    }

    override fun getItemCount(): Int {
        return data.size
    }
}