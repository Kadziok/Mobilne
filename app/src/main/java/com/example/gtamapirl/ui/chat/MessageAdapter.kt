package com.example.gtamapirl.ui.chat

import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.gtamapirl.R
import com.example.gtamapirl.data.Message
import com.example.gtamapirl.databinding.ItemMessageBinding
import com.firebase.ui.database.FirebaseRecyclerAdapter
import com.firebase.ui.database.FirebaseRecyclerOptions

class MessageAdapter(options: FirebaseRecyclerOptions<Message>,
                     val fragment: ChatFragment, val myId: String):
    FirebaseRecyclerAdapter<Message, RecyclerView.ViewHolder>(options) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val view = inflater.inflate(R.layout.item_message, parent, false)
        val binding = ItemMessageBinding.bind(view)
        return MessageHolder(binding)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int, model: Message) {
        (holder as MessageAdapter.MessageHolder).bind(model)
    }

    inner class MessageHolder(private val binding: ItemMessageBinding) : RecyclerView.ViewHolder(binding.root) {

        fun bind(item: Message) {
            binding.messageTextView.text = item.text
            setTextColor(item.userid, binding.messageTextView)
        }

        private fun setTextColor(userId: String?, textView: TextView) {
            if (myId == userId) {
                textView.setBackgroundResource(R.drawable.message_color)
                textView.setTextColor(Color.WHITE)
            } else {
                textView.setBackgroundResource(R.drawable.message_gray)
                textView.setTextColor(Color.BLACK)
            }
        }
    }

}