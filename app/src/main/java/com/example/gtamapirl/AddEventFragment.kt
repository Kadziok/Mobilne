package com.example.gtamapirl

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.gtamapirl.databinding.FragmentAddEventBinding
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.wdullaer.materialdatetimepicker.date.DatePickerDialog
import java.time.Instant
import java.time.LocalDate
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import java.util.*
import kotlin.random.Random

class AddEventFragment : Fragment() {

    private var date: LocalDate? = null
    private var binding: FragmentAddEventBinding? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_add_event, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
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

    }

    private fun setDate() {
        val now = Calendar.getInstance()
        val currentYear: Int = now.get(Calendar.YEAR)
        val currentMonth: Int = now.get(Calendar.MONTH)
        val currentDay: Int = now.get(Calendar.DAY_OF_MONTH)
        val datePickerDialog = DatePickerDialog.newInstance({ _, year, monthOfYear, dayOfMonth ->
            date = LocalDate.of(year, monthOfYear, dayOfMonth)
            binding!!.dateInput.setText(date?.format(DateTimeFormatter.ofPattern("yyyy-MM-dd")))
            binding!!.dateInputLayout.isErrorEnabled = false
        }, currentYear, currentMonth, currentDay)
        datePickerDialog.setTitle(getString(R.string.addEvent_datePicker_title))
        datePickerDialog.accentColor = resources.getColor(R.color.colorPrimary)

        datePickerDialog.show(childFragmentManager, null)
    }

    private fun addEvent() {
        binding!!.textInputNameLayout.isErrorEnabled = false
        binding!!.dateInputLayout.isErrorEnabled = false

        if(binding!!.textInputName.text!!.isEmpty()) {
            binding!!.textInputNameLayout.isErrorEnabled = true
            binding!!.textInputNameLayout.error = getString(R.string.addEvent_error_name_blank)
        }
        else if(date==null) {
            binding!!.dateInputLayout.isErrorEnabled = true
            binding!!.dateInputLayout.error = getString(R.string.addEvent_error_date_empty)
        }
        else {
            val id = DateTimeFormatter.ISO_INSTANT.format(Instant.now()).toString() + Random.nextInt().toString()
            val userId = binding!!.textInputName.text.toString()

            val newEvent = EventData(
                id,
                userId,
                FirebaseAuth.getInstance().currentUser!!.uid,
                date.toString(),
                "Brak czasu",
                0f,
                0f,
                binding!!.textInputDesc.text.toString()
            )

            val db = FirebaseDatabase.getInstance().reference
            db.child("events").child(userId).child(id).setValue(newEvent)
        }
    }

    companion object {
        fun setLocation(location2: String) {}
    }


}
