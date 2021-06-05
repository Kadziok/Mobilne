package com.example.gtamapirl.ui.chats.chat

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.navArgs
import com.example.gtamapirl.R
import com.example.gtamapirl.databinding.FragmentChatBinding


class ChatFragment : Fragment() {
    private var binding: FragmentChatBinding? = null
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
        binding!!.idChat.text = args.idChat!!
    }

    companion object {

        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            ChatFragment().apply {}
    }
}