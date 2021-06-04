package com.example.gtamapirl

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.navArgs
import com.example.gtamapirl.databinding.FragmentAddEventBinding
import com.example.gtamapirl.databinding.FragmentEventBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase


class EventFragment : Fragment() {

    private var binding: FragmentEventBinding? = null
    private val args: EventFragmentArgs by navArgs()
    private lateinit var eventId: String


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_event, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentEventBinding.bind(view)
        eventId = args.idEvent

        val db = Firebase.database

        db.reference.child("events").child(eventId).addValueEventListener(object: ValueEventListener{
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                
            }
            override fun onCancelled(databaseError: DatabaseError) {
                // Getting Post failed, log a message
                Log.w("TAG", "loadPost:onCancelled", databaseError.toException())
            }
        })




        val usersEvents = db.reference.child("user_events")
        usersEvents.addValueEventListener(object: ValueEventListener{
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                for(item in dataSnapshot.children) {
                    if(item.child(eventId).exists()) {
                        db.reference
                            .child("users")
                            .child(item.key.toString())
                            .child("name")
                            .get().addOnSuccessListener {
                                //binding!!.eventID.text = "\n ${it.value}"
                            }
                    }
                }
            }
            override fun onCancelled(databaseError: DatabaseError) {
                // Getting Post failed, log a message
                Log.w("TAG", "loadPost:onCancelled", databaseError.toException())
            }
        })
    }

    companion object {
        fun newInstance(param1: String, param2: String) =
            EventFragment().apply {}
    }
}