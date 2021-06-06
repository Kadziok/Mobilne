package com.example.gtamapirl.ui.chat

import android.graphics.Color
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.gtamapirl.R
import com.example.gtamapirl.data.MessageData
import com.example.gtamapirl.databinding.ItemMessageBinding
import com.firebase.ui.database.FirebaseRecyclerAdapter
import com.firebase.ui.database.FirebaseRecyclerOptions
import java.sql.Date
import java.text.SimpleDateFormat
import java.util.*

class MessageAdapter(options: FirebaseRecyclerOptions<MessageData>,
                     val fragment: ChatFragment, val myId: String):
    FirebaseRecyclerAdapter<MessageData, RecyclerView.ViewHolder>(options) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val view = inflater.inflate(R.layout.item_message, parent, false)
        val binding = ItemMessageBinding.bind(view)
        return MessageHolder(binding)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int, model: MessageData) {
        (holder as MessageAdapter.MessageHolder).bind(model)
    }

    inner class MessageHolder(private val binding: ItemMessageBinding) : RecyclerView.ViewHolder(binding.root) {

        fun bind(item: MessageData) {
            binding.messageTextView.text = item.text
            setTextColor(item.userid, binding.messageTextView, binding.msg)
            if (item.timeStamp != null) {
                val timestamp = Date(item.timeStamp!!)
                val dateString = timestamp.dateToString("E: hh:mm")
                binding.timestamp.text = dateString.toString()
            } else {
                binding.timestamp.text = "Before timestamp feature"
            }

            binding.messageLay.setOnClickListener {
                if (binding.timestamp.visibility == View.GONE){
                    binding.timestamp.visibility = View.VISIBLE
                } else {
                    binding.timestamp.visibility = View.GONE
                }

            }
        }

        private fun setTextColor(userId: String?, textView: TextView, msg: LinearLayout) {
            if (myId == userId) {
                textView.setBackgroundResource(R.drawable.message_color)
                textView.setTextColor(Color.WHITE)
                msg.gravity = Gravity.RIGHT
            } else {
                textView.setBackgroundResource(R.drawable.message_gray)
                textView.setTextColor(Color.BLACK)
                msg.gravity = Gravity.LEFT
            }
        }

        private fun Date.dateToString(s: String): Any {
            val dateFormatter = SimpleDateFormat(s, Locale.getDefault())
            return dateFormatter.format(this)
        }

    }
}

