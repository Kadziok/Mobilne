package com.example.gtamapirl

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RadioGroup
import android.widget.Toast
import androidx.navigation.fragment.navArgs
import com.example.gtamapirl.databinding.FragmentEventBinding
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.database.ktx.getValue
import com.google.firebase.ktx.Firebase


class EventFragment : Fragment() {

    private var binding: FragmentEventBinding? = null
    private val args: EventFragmentArgs by navArgs()
    private lateinit var eventId: String
    private lateinit var db: FirebaseDatabase
    private lateinit var cUser: FirebaseUser
    private lateinit var callback: OnMapReadyCallback


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
        cUser = FirebaseAuth.getInstance().currentUser!!
        db = Firebase.database

        binding!!.deleteEvent.setOnClickListener { deleteEvent() }
        binding!!.radioGroup2.setOnCheckedChangeListener { group, checkedId ->
            choiceChanged(group, checkedId)
        }

        /***
         * Spradzenie, czy użytkownik jest hostem
         * i może edytować wydarzenie
         */
        db.reference.child("user_events")
            .child(cUser.uid)
            .child(eventId)
            .child("state")
            .addValueEventListener(object: ValueEventListener{
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val status = dataSnapshot.getValue<String>().toString()
                if (status == "host") {
                    binding!!.eventName.isEnabled = true
                    binding!!.eventDesc.isEnabled = true
                    binding!!.eventDate.isEnabled = true
                    binding!!.eventTime.isEnabled = true
                    binding!!.radioGroup2.visibility = View.GONE
                    binding!!.deleteEvent.visibility = View.VISIBLE
                } else {
                    binding!!.eventName.isEnabled = false
                    binding!!.eventDesc.isEnabled = false
                    binding!!.eventDate.isEnabled = false
                    binding!!.eventTime.isEnabled = false
                    binding!!.radioGroup2.visibility = View.VISIBLE
                    binding!!.deleteEvent.visibility = View.GONE
                }
            }
            override fun onCancelled(databaseError: DatabaseError) {
                Log.w("TAG", "loadPost:onCancelled", databaseError.toException())
            }
        })

        /***
         * Pobranie danych wydarzenia
         */
        db.reference.child("events").child(eventId).addValueEventListener(object: ValueEventListener{
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val event = dataSnapshot.getValue<EventData>()
                if (event?.id != null) {
                    binding!!.eventName.setText(event!!.name)
                    binding!!.eventDesc.setText(event!!.description)
                    binding!!.eventDate.setText(event!!.date)
                    binding!!.eventTime.setText(event!!.time)

                    callback = OnMapReadyCallback { map ->
                        val latLng = LatLng(event.latitude!!.toDouble(), event.longitude!!.toDouble())
                        val cameraPosition = CameraPosition.Builder()
                            .target(latLng)
                            .zoom(15f)
                            .build()
                        map.moveCamera(CameraUpdateFactory.newCameraPosition(cameraPosition))
                        map.addMarker(
                            MarkerOptions()
                            .position(latLng)
                        )
                    }

                    val mapFragment = childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment?
                    mapFragment?.getMapAsync(callback)

                }
            }
            override fun onCancelled(databaseError: DatabaseError) {
                // Getting Post failed, log a message
                Log.w("TAG", "loadPost:onCancelled", databaseError.toException())
            }
        })

        /***
         * Pobranie danych o innych uczestnikach
         */
        var usersEvents = db.reference.child("user_events")
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

        /***
         * Pobranie statusu udziału w wydarzeniu
         */
        usersEvents = db.reference.child("user_events")
        usersEvents.addValueEventListener(object: ValueEventListener{
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                var choice = binding!!.radioButton3.id
                if(dataSnapshot.child(cUser.uid).child(eventId).exists()) {
                    val event = dataSnapshot.child(cUser.uid).child(eventId).getValue<EventElement>()
                    when (event?.state) {
                        "attends" -> choice = binding!!.radioButton.id
                        "interested" -> choice = binding!!.radioButton2.id
                    }
                }
                binding!!.radioGroup2.check(choice)
            }
            override fun onCancelled(databaseError: DatabaseError) {
                // Getting Post failed, log a message
                Log.w("TAG", "loadPost:onCancelled", databaseError.toException())
            }
        })

    }

    fun deleteEvent() {
        val usersEvents = db.reference.child("user_events")
        usersEvents.addValueEventListener(object: ValueEventListener{
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                for(item in dataSnapshot.children) {
                    if(item.child(eventId).exists()) {
                        db.reference
                            .child("user_events")
                            .child(item.key.toString())
                            .child(eventId)
                            .removeValue()
                    }
                }
            }
            override fun onCancelled(databaseError: DatabaseError) {
                // Getting Post failed, log a message
                Log.w("TAG", "loadPost:onCancelled", databaseError.toException())
            }
        })
        db.reference
            .child("events")
            .child(eventId)
            .removeValue()
    }

    fun choiceChanged(group: RadioGroup, checkedId: Int) {
        when (checkedId) {
            binding!!.radioButton.id ->
                db.reference.child("user_events")
                    .child(cUser.uid)
                    .child(eventId)
                    .setValue(EventElement(eventId, "attends"))

            binding!!.radioButton2.id ->
                db.reference.child("user_events")
                    .child(cUser.uid)
                    .child(eventId)
                    .setValue(EventElement(eventId, "interested"))

            binding!!.radioButton3.id ->
                db.reference.child("user_events")
                    .child(cUser.uid)
                    .child(eventId)
                    .removeValue()
        }
    }

    companion object {
        fun newInstance(param1: String, param2: String) =
            EventFragment().apply {}
    }
}