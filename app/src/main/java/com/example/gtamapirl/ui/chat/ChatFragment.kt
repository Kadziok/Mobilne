package com.example.gtamapirl.ui.chat

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.gtamapirl.R
import com.example.gtamapirl.data.Message
import com.example.gtamapirl.databinding.FragmentChatBinding
import com.firebase.ui.database.FirebaseRecyclerOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase

class ChatFragment : Fragment() {

    private lateinit var cUser: FirebaseUser
    private lateinit var adapter: MessageAdapter
    private lateinit var binding: FragmentChatBinding
    private val args: ChatFragmentArgs by navArgs()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_chat, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentChatBinding.bind(view)
        cUser = FirebaseAuth.getInstance().currentUser!!
        val db = Firebase.database

        val id = args.idChat
        val messagesRef = db.reference.child("chats").child(id).child("messages")
        val options = FirebaseRecyclerOptions.Builder<Message>()
            .setQuery(messagesRef, Message::class.java)
            .build()

        adapter = MessageAdapter(options, this, cUser.uid)
        adapter.startListening()

        binding.progressBar.visibility = ProgressBar.INVISIBLE
        val manager = LinearLayoutManager(view.context)
        manager.stackFromEnd = true
        binding.messageRecyclerView.layoutManager = manager
        binding.messageRecyclerView.adapter = adapter
        ScrollToBottomObserver(binding.messageRecyclerView, adapter, manager)
        binding.messageEditText.addTextChangedListener(SendButtonObserver(binding.sendButton))

        binding.sendButton.setOnClickListener {
            val message = Message(
                binding.messageEditText.text.toString(),
                cUser.uid)
            db.reference.child("chats").child(id).child("messages").push().setValue(message)
            binding.messageEditText.setText("")
        }
    }

}