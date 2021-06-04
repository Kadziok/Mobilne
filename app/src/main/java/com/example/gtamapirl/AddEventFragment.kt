package com.example.gtamapirl

import android.os.Bundle
import android.text.format.Time
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.gtamapirl.databinding.FragmentAddEventBinding
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.wdullaer.materialdatetimepicker.date.DatePickerDialog
import com.wdullaer.materialdatetimepicker.time.TimePickerDialog
import java.time.LocalDate
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import java.util.*

class AddEventFragment : Fragment() {

    private var date: LocalDate? = null
    private var time: LocalTime? = null
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


        binding!!.timeInput.setOnClickListener {
            setTime()
        }
        binding!!.timeInput.setOnFocusChangeListener { _: View, focused: Boolean ->
            if(focused)
                setTime()
        }
        binding!!.timeInputLayout.setEndIconOnClickListener {
            date = null
            binding!!.dateInput.setText("")
        }

    }

    private fun setDate() {
        val now = Calendar.getInstance()
        val currentYear: Int = now.get(Calendar.YEAR)
        val currentMonth: Int = now.get(Calendar.MONTH)
        val currentDay: Int = now.get(Calendar.DAY_OF_MONTH)
        val dateListener = DatePickerDialog.OnDateSetListener { _, year, monthOfYear, dayOfMonth ->
            date = LocalDate.of(year, monthOfYear, dayOfMonth)
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
                //TODO adding event
            }
        }
    }

    companion object {
        fun setLocation(location2: String) {}
    }


}
