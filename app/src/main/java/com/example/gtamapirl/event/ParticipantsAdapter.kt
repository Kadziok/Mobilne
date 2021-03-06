package com.example.gtamapirl.event

import android.graphics.BitmapFactory
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.graphics.scale
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.example.gtamapirl.R
import com.example.gtamapirl.data.ParticipantData
import com.example.gtamapirl.ui.add_event.AddEventFragmentDirections
import com.example.gtamapirl.ui.event.EventFragment
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import java.io.File
import java.util.*
import kotlin.collections.ArrayList

class ParticipantsAdapter (private val data: ArrayList<ParticipantData>, parent: EventFragment)
    : RecyclerView.Adapter<ParticipantsAdapter.ViewHolder>() {

    private val storage = Firebase.storage.reference
    val parent = parent

    class ViewHolder(view: View, parent : ParticipantsAdapter) : RecyclerView.ViewHolder(view) {
        val name: TextView
        val state : TextView
        val icon : ImageView
        var id : String

        init {
            name = view.findViewById(R.id.name)
            state = view.findViewById(R.id.status)
            icon = view.findViewById(R.id.chatIcon)
            id = ""
            view.setOnClickListener {
                parent.parent.createChat(id)
            }
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

        var defaultIconRef = storage.child("icons/${data[position].icon}")
        val localFile: File = File.createTempFile("tmp", "jpg")

        defaultIconRef.getFile(localFile)
                .addOnSuccessListener {
                    val bmp = BitmapFactory.decodeFile(localFile.absolutePath).scale(400, 400)
                    holder.icon.setImageBitmap(bmp)
                }

    }

    override fun getItemCount(): Int {
        return data.size
    }
}