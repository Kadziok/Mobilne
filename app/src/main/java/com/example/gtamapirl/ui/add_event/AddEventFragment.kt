package com.example.gtamapirl.ui.add_event

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.example.gtamapirl.R
import com.example.gtamapirl.data.EventData
import com.example.gtamapirl.data.UserEventData
import com.example.gtamapirl.databinding.FragmentAddEventBinding
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.*
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.wdullaer.materialdatetimepicker.date.DatePickerDialog
import com.wdullaer.materialdatetimepicker.time.TimePickerDialog
import java.time.LocalDate
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import java.util.*
import kotlin.random.Random.Default.nextInt


class AddEventFragment : Fragment() {

    private var date: LocalDate? = null
    private var time: LocalTime? = null
    private var iconName: String = "1"
    private var binding: FragmentAddEventBinding? = null
    private val args: AddEventFragmentArgs by navArgs()
    private var mapFragment: SupportMapFragment? = null
    private var icon: BitmapDescriptor? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_add_event, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        icon = loadMarkerIcon(R.drawable.marker1)

        mapFragment = childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment?
        mapFragment?.getMapAsync(getCallback())

        binding = FragmentAddEventBinding.bind(view)

        binding!!.fab.setOnClickListener {
            addEvent()
        }
        binding!!.dateInput.setOnClickListener {
            setDate()
        }
        binding!!.dateInput.setOnFocusChangeListener { _: View, focused: Boolean ->
            if(focused)
                setDate()
        }
        binding!!.dateInputLayout.setEndIconOnClickListener {
            date = null
            binding!!.dateInput.setText("")
        }
        binding!!.timeInput.setOnClickListener {
            setTime()
        }
        binding!!.timeInput.setOnFocusChangeListener { _: View, focused: Boolean ->
            if(focused)
                setTime()
        }
        binding!!.timeInputLayout.setEndIconOnClickListener {
            time = null
            binding!!.timeInput.setText("")
        }

        setMarkerImage(binding!!.marker0, "0", R.drawable.marker0)
        setMarkerImage(binding!!.marker1, "1", R.drawable.marker1)
        setMarkerImage(binding!!.marker2, "2", R.drawable.marker2)
        setMarkerImage(binding!!.marker3, "3", R.drawable.marker3)
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

    private fun setDate() {
        val now = Calendar.getInstance()
        val currentYear: Int = now.get(Calendar.YEAR)
        val currentMonth: Int = now.get(Calendar.MONTH)
        val currentDay: Int = now.get(Calendar.DAY_OF_MONTH)
        val dateListener = DatePickerDialog.OnDateSetListener { _, year, monthOfYear, dayOfMonth ->
            date = LocalDate.of(year, monthOfYear + 1, dayOfMonth)
            binding!!.dateInput.setText(date?.format(DateTimeFormatter.ofPattern("yyyy-MM-dd")))
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
            binding!!.timeInput.setText(time?.format(DateTimeFormatter.ofPattern("HH:mm")))
            binding!!.timeInputLayout.isErrorEnabled = false
        }
        val timePickerDialog = TimePickerDialog.newInstance(timeListener, currentHour, currentMinute, false)
        timePickerDialog.title = getString(R.string.addEvent_timePicker_title)
        timePickerDialog.accentColor = resources.getColor(R.color.colorPrimary)

        timePickerDialog.show(childFragmentManager, null)
    }

    private fun addEvent() {
        binding!!.textInputNameLayout.isErrorEnabled = false
        binding!!.dateInputLayout.isErrorEnabled = false

        when {
            binding!!.textInputName.text!!.isEmpty() -> {
                binding!!.textInputNameLayout.isErrorEnabled = true
                binding!!.textInputNameLayout.error = getString(R.string.addEvent_error_name_blank)
            }
            date==null -> {
                binding!!.dateInputLayout.isErrorEnabled = true
                binding!!.dateInputLayout.error = getString(R.string.addEvent_error_date_empty)
            }
            time==null -> {
                binding!!.timeInputLayout.isErrorEnabled = true
                binding!!.timeInputLayout.error = getString(R.string.addEvent_error_time_empty)
            }
            else -> {
                val id = System.currentTimeMillis().toString() + nextInt().toString()
                val userId = FirebaseAuth.getInstance().currentUser!!.uid
                val newEvent = EventData(
                    id,
                    binding!!.textInputName.text!!.toString(),
                    userId,
                    date.toString(),
                    time.toString(),
                    args.latitude.toDouble(),
                    args.longitude.toDouble(),
                    binding!!.textInputDesc.text.toString(),
                    1,
                    iconName
                )

                val db = FirebaseDatabase.getInstance().reference
                db.child("events").child(id).setValue(newEvent)
                db.child("user_events").child(userId).child(id).setValue(UserEventData(id, "host"))
                val action = AddEventFragmentDirections.actionGoToEvent(id)
                findNavController().popBackStack()
                findNavController().navigate(action)
            }
        }
    }

    private fun getCallback(): OnMapReadyCallback {
        return OnMapReadyCallback { map ->
            map.clear()
            val latLng = LatLng(args.latitude.toDouble(), args.longitude.toDouble())
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

    private fun loadMarkerIcon(res: Int): BitmapDescriptor {
        val bm = BitmapFactory.decodeResource(resources, res)
        val resizedBitmap = Bitmap.createScaledBitmap(bm, 100, 100, false)
        return BitmapDescriptorFactory.fromBitmap(resizedBitmap)
    }
}
