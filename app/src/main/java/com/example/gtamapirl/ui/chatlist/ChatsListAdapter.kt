package com.example.gtamapirl.ui.chatlist

import android.content.Context
import android.graphics.BitmapFactory
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import androidx.core.graphics.scale
import androidx.recyclerview.widget.RecyclerView
import com.example.gtamapirl.R
import com.example.gtamapirl.data.ChatData
import com.example.gtamapirl.databinding.ItemChatBinding
import com.example.gtamapirl.ui.chatlist.ChatsListFragment
import com.firebase.ui.database.FirebaseRecyclerAdapter
import com.firebase.ui.database.FirebaseRecyclerOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import java.io.File


class ChatsListAdapter(private val options: FirebaseRecyclerOptions<ChatData>, val fragment : ChatsListFragment) :
    FirebaseRecyclerAdapter<ChatData, RecyclerView.ViewHolder>(options) {
    lateinit var context : Context

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val view = inflater.inflate(R.layout.item_chat, parent, false)

        val binding = ItemChatBinding.bind(view)
        context = view.context
        return ChatViewHolder(binding)
    }

    inner class ChatViewHolder(private val binding: ItemChatBinding) : RecyclerView.ViewHolder(
        binding.root
    ) {

        fun bind(item: ChatData) {
            val db = Firebase.database.reference
            var cUser = FirebaseAuth.getInstance().currentUser

            if (cUser!!.uid == item.user1) {
                db.child("users").child(item.user2!!).child("name").get().addOnSuccessListener {
                    binding.name.text = it.value.toString()
                }
                db.child("users").child(item.user2!!).child("icon").get().addOnSuccessListener {
                    loadIcon(binding!!.chatIcon, it.value.toString())
                }

            } else if (cUser!!.uid == item.user2) {
                db.child("users").child(item.user1!!).child("name").get().addOnSuccessListener {
                    binding.name.text = it.value.toString()
                }
                db.child("users").child(item.user1!!).child("icon").get().addOnSuccessListener {
                    loadIcon(binding!!.chatIcon, it.value.toString())
                }
            }


            binding.chatLay.setOnClickListener {
                fragment.goToChat(item.id!!)
            }
        }

    }

    private fun loadIcon(chatIcon: ImageView, icon: String) {
        val storage = Firebase.storage.reference
        var iconRef = storage.child("icons/${icon}")
        val localFile: File = File.createTempFile("tmp", "jpg")

        iconRef.getFile(localFile).addOnSuccessListener {
            val bmp = BitmapFactory.decodeFile(localFile.absolutePath).scale(400, 400)
            chatIcon.setImageBitmap(bmp)
        }.addOnFailureListener{
            Log.e("firebase", "Error getting data", it)
        }
    }

    override fun onBindViewHolder(
        holder: RecyclerView.ViewHolder,
        position: Int,
        model: ChatData
    ) {
        (holder as ChatViewHolder).bind(model)
    }

}