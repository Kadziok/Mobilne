package com.example.gtamapirl.ui.chatlist

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.gtamapirl.R
import com.example.gtamapirl.data.ChatData
import com.example.gtamapirl.ui.chatlist.ChatsListAdapter
import com.firebase.ui.database.FirebaseRecyclerOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase

class ChatsListFragment : Fragment() {
    private lateinit var cUser: FirebaseUser
    private lateinit var adapter: ChatsListAdapter

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        cUser = FirebaseAuth.getInstance().currentUser!!
        val db = Firebase.database

        val chatsRef = db.reference.child("users").child(cUser.uid).child("chats")
        val options = FirebaseRecyclerOptions.Builder<ChatData>()
            .setQuery(chatsRef, ChatData::class.java)
            .build()

        adapter = ChatsListAdapter(options, this)
        adapter.startListening()

        val manager = LinearLayoutManager(view.context)
        val recycler = view.findViewById<RecyclerView>(R.id.chatsRecyclerView)

        recycler.layoutManager = manager
        recycler.adapter = adapter
    }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_chatlist, container, false)
    }

    override fun onPause() {
        adapter.stopListening()
        super.onPause()
    }

    override fun onResume() {
        super.onResume()
        adapter.startListening()
    }

    fun goToChat(id: String) {
        val action = ChatsListFragmentDirections.actionNavChatsToFragmentChat(id)
        findNavController().navigate(action)
    }

}