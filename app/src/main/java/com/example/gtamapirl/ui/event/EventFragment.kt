package com.example.gtamapirl.ui.event

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.navigation.fragment.findNavController
import android.widget.RadioGroup
import androidx.core.view.isVisible
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.LinearSnapHelper
import androidx.recyclerview.widget.SnapHelper
import com.example.gtamapirl.MainActivity
import com.example.gtamapirl.R
import com.example.gtamapirl.data.EventData
import com.example.gtamapirl.data.ParticipantData
import com.example.gtamapirl.data.UserEventData
import com.example.gtamapirl.databinding.FragmentEventBinding
import com.example.gtamapirl.event.ParticipantsAdapter
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.*
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.database.ktx.getValue
import com.google.firebase.ktx.Firebase
import com.wdullaer.materialdatetimepicker.date.DatePickerDialog
import com.wdullaer.materialdatetimepicker.time.TimePickerDialog
import java.time.LocalDate
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import java.util.*
import kotlin.collections.ArrayList
import kotlin.random.Random


class EventFragment : Fragment() {

    private var binding: FragmentEventBinding? = null
    private val args: EventFragmentArgs by navArgs()
    private lateinit var eventId: String
    private lateinit var db: FirebaseDatabase
    private lateinit var cUser: FirebaseUser
    private lateinit var callback: OnMapReadyCallback
    private lateinit var latLng: LatLng
    private var eventName: String? = null
    private var statusSet: Boolean = false
    private var dataSet: Boolean = false
    private var recyclerSet: Boolean = false
    private var date: LocalDate? = null
    private var time: LocalTime? = null
    private val participants = ArrayList<ParticipantData>()
    private var mapFragment: SupportMapFragment? = null
    private var icon: BitmapDescriptor? = null
    private var iconName: String = "1"
    private var latitude: Double? = null
    private var longitude: Double? = null

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

        val recycler = binding!!.participantsRecycler
        recycler.layoutManager = LinearLayoutManager(
            context,
            LinearLayoutManager.VERTICAL,
            false
        )
        recycler.adapter = ParticipantsAdapter(participants)
        //val helper: SnapHelper = LinearSnapHelper()
        //helper.attachToRecyclerView(recycler)


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
                    binding!!.eventDesc.isEnabled = true
                    binding!!.eventDate.isEnabled = true
                    binding!!.eventTime.isEnabled = true
                    binding!!.radioGroup2.visibility = View.GONE
                    binding!!.deleteEvent.visibility = View.VISIBLE
                    binding!!.markers.visibility = View.VISIBLE
                    binding!!.saveChanges.visibility = View.VISIBLE

                    setMarkerImage(binding!!.marker0, "0", R.drawable.marker0)
                    setMarkerImage(binding!!.marker1, "1", R.drawable.marker1)
                    setMarkerImage(binding!!.marker2, "2", R.drawable.marker2)
                    setMarkerImage(binding!!.marker3, "3", R.drawable.marker3)

                    binding!!.deleteEvent.setOnClickListener {
                        deleteEvent()
                        findNavController().popBackStack()
                    }
                    binding!!.saveChanges.setOnClickListener {
                        saveChanges()
                        findNavController().popBackStack()
                    }

                    binding!!.eventDate.setOnClickListener {
                        setDate()
                    }
                    binding!!.eventDate.setOnFocusChangeListener { _: View, focused: Boolean ->
                        if(focused)
                            setDate()
                    }
                    binding!!.eventTime.setOnClickListener {
                        setTime()
                    }
                    binding!!.eventTime.setOnFocusChangeListener { _: View, focused: Boolean ->
                        if(focused)
                            setTime()
                    }
                } else {
                    binding!!.eventDesc.isEnabled = false
                    binding!!.eventDate.isEnabled = false
                    binding!!.eventTime.isEnabled = false
                    binding!!.radioGroup2.visibility = View.VISIBLE
                    binding!!.deleteEvent.visibility = View.GONE
                    binding!!.markers.visibility = View.GONE
                    binding!!.saveChanges.visibility = View.GONE

                    binding!!.radioGroup2.setOnCheckedChangeListener { group, checkedId ->
                        choiceChanged(group, checkedId)
                    }
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
                if (!dataSet) {
                    val event = dataSnapshot.getValue<EventData>()
                    if (event?.id != null) {
                        if (event.name == null){
                            event.name = "Event"
                        }
                        (activity as MainActivity).updateTitle(event.name!!)

                        eventName = event.name
                        binding!!.eventDesc.setText(event.description)
                        binding!!.eventDate.setText(event.date)
                        binding!!.eventTime.setText(event.time)
                        latitude = event.latitude!!.toDouble()
                        longitude = event.longitude!!.toDouble()

                        when (event.iconName) {
                            "0" -> {
                                icon = loadMarkerIcon(R.drawable.marker0)
                                iconName = "0"
                            }
                            "1" -> {
                                icon = loadMarkerIcon(R.drawable.marker1)
                                iconName = "1"
                            }
                            "2" -> {
                                icon = loadMarkerIcon(R.drawable.marker2)
                                iconName = "2"
                            }
                            "3" -> {
                                icon = loadMarkerIcon(R.drawable.marker3)
                                iconName = "3"
                            }
                        }

                        mapFragment = childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment?
                        mapFragment?.getMapAsync(getCallback())

                        dataSet = true
                    }
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
                if (!recyclerSet) {
                    for (item in dataSnapshot.children) {
                        if (item.child(eventId).exists()) {
                            val userEventData = item.child(eventId).getValue<UserEventData>()
                            db.reference
                                .child("users")
                                .child(item.key.toString())
                                .child("name")
                                .get().addOnSuccessListener { it ->
                                        val username = it.value.toString()
                                        db.reference
                                                .child("users")
                                                .child(item.key.toString())
                                                .child("icon")
                                                .get().addOnSuccessListener { it2 ->
                                                    val icon = it2.value.toString()
                                                    participants.add(
                                                            ParticipantData(
                                                    item.key.toString(),
                                                    username,
                                                    userEventData?.state!!,
                                                    icon
                                            )
                                        )
                                        binding!!.participantsRecycler.adapter?.notifyDataSetChanged()
                                    }
                                }
                        }
                    }

                    recyclerSet = true
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
                if (!statusSet) {
                    var choice = binding!!.radioButton3.id
                    if (dataSnapshot.child(cUser.uid).child(eventId).exists()) {
                        val event =
                            dataSnapshot.child(cUser.uid).child(eventId).getValue<UserEventData>()
                        when (event?.state) {
                            "attends" -> choice = binding!!.radioButton.id
                            "interested" -> choice = binding!!.radioButton2.id
                        }
                    }
                    binding!!.radioGroup2.check(choice)

                    statusSet = true
                }
            }
            override fun onCancelled(databaseError: DatabaseError) {
                // Getting Post failed, log a message
                Log.w("TAG", "loadPost:onCancelled", databaseError.toException())
            }
        })

    }

    private fun loadMarkerIcon(res: Int): BitmapDescriptor {
        val bm = BitmapFactory.decodeResource(resources, res)
        val resizedBitmap = Bitmap.createScaledBitmap(bm, 100, 100, false)
        return BitmapDescriptorFactory.fromBitmap(resizedBitmap)
    }

    private fun setMarkerImage(markerImage: ImageView, s: String, res: Int) {
        var bm = BitmapFactory.decodeResource(resources, res)
        val resizedBitmap = Bitmap.createScaledBitmap(bm, 200, 200, false)
        markerImage.setImageBitmap(resizedBitmap)
        markerImage.setOnClickListener{
            iconName = s
            icon = loadMarkerIcon(res)
            mapFragment = childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment?
            mapFragment?.getMapAsync(getCallback())
        }
    }
    private fun deleteEvent() {
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

    private fun saveChanges() {
        binding!!.dateInputLayout.isErrorEnabled = false
        val userId = FirebaseAuth.getInstance().currentUser!!.uid
        val newEvent = EventData(
            eventId,
            eventName!!,
            userId,
            binding!!.eventDate.text.toString(),
            binding!!.eventTime.text.toString(),
            latitude!!,
            longitude!!,
            binding!!.eventDesc.text.toString(),
            iconName
        )

        val db = FirebaseDatabase.getInstance().reference
        db.child("events").child(eventId).setValue(newEvent)
    }

    private fun choiceChanged(group: RadioGroup, checkedId: Int) {
        if (statusSet) {
            when (checkedId) {
                binding!!.radioButton.id ->
                    db.reference.child("user_events")
                        .child(cUser.uid)
                        .child(eventId)
                        .setValue(UserEventData(eventId, "attends"))

                binding!!.radioButton2.id ->
                    db.reference.child("user_events")
                        .child(cUser.uid)
                        .child(eventId)
                        .setValue(UserEventData(eventId, "interested"))

                binding!!.radioButton3.id ->
                    db.reference.child("user_events")
                        .child(cUser.uid)
                        .child(eventId)
                        .removeValue()
            }
        }
    }

    private fun setDate() {
        val now = Calendar.getInstance()
        val currentYear: Int = now.get(Calendar.YEAR)
        val currentMonth: Int = now.get(Calendar.MONTH)
        val currentDay: Int = now.get(Calendar.DAY_OF_MONTH)
        val dateListener = DatePickerDialog.OnDateSetListener { _, year, monthOfYear, dayOfMonth ->
            date = LocalDate.of(year, monthOfYear + 1, dayOfMonth)
            binding!!.eventDate.setText(date?.format(DateTimeFormatter.ofPattern("yyyy-MM-dd")))
            binding!!.dateInputLayout.isErrorEnabled = false
        }
        val datePickerDialog = DatePickerDialog.newInstance(dateListener, currentYear, currentMonth, currentDay)
        datePickerDialog.setTitle(getString(R.string.addEvent_datePicker_title))
        datePickerDialog.accentColor = resources.getColor(R.color.colorPrimary)
        datePickerDialog.show(childFragmentManager, null)
    }

    private fun setTime() {
        val now = Calendar.getInstance()
        val currentHour: Int = now.get(Calendar.HOUR_OF_DAY)
        val currentMinute: Int = now.get(Calendar.MINUTE)
        val timeListener = TimePickerDialog.OnTimeSetListener { _, hour, minute, _ ->
            time = LocalTime.of(hour, minute, 0)
            binding!!.eventTime.setText(time?.format(DateTimeFormatter.ofPattern("HH:mm")))
            binding!!.timeInputLayout.isErrorEnabled = false
        }
        val timePickerDialog = TimePickerDialog.newInstance(timeListener, currentHour, currentMinute, false)
        timePickerDialog.title = getString(R.string.addEvent_timePicker_title)
        timePickerDialog.accentColor = resources.getColor(R.color.colorPrimary)

        timePickerDialog.show(childFragmentManager, null)
    }

    private fun getCallback(): OnMapReadyCallback {
        return OnMapReadyCallback { map ->
            map.clear()
            val latLng = LatLng(latitude!!, longitude!!)
            val cameraPosition = CameraPosition.Builder()
                .target(latLng)
                .zoom(15f)
                .build()
            map.moveCamera(CameraUpdateFactory.newCameraPosition(cameraPosition))
            map.addMarker(MarkerOptions()
                .position(latLng)
                .icon(icon)
            )
        }
    }

    companion object {
        fun newInstance(param1: String, param2: String) =
            EventFragment().apply {}
    }
}