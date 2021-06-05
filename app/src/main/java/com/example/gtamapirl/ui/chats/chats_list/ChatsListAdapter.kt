package com.example.gtamapirl.ui.chats

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.gtamapirl.R
import com.example.gtamapirl.data.ChatData
import com.example.gtamapirl.databinding.ItemChatBinding
import com.example.gtamapirl.ui.chats.chats_list.ChatsListFragment
import com.firebase.ui.database.FirebaseRecyclerAdapter
import com.firebase.ui.database.FirebaseRecyclerOptions


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
            binding.name.text = item.id

            binding.chatLay.setOnClickListener {
                fragment.goToChat(item.id!!)
            }
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