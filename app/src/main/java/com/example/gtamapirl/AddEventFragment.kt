package com.example.gtamapirl

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.wdullaer.materialdatetimepicker.date.DatePickerDialog
import java.util.*

var location: String = "null"

class AddEventFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_add_event, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        var yearFrom: Int = -1
        var monthFrom: Int = -1
        var dayFrom: Int = -1
        var yearTo: Int = -1
        var monthTo: Int = -1
        var dayTo: Int = -1

        //TODO USTAW LOKALIZACJE LABEL POTRZEBA CHYBA POBRAC Z MAP FRAGMENT

        view.findViewById<TextView>(R.id.location).text = location

        view.findViewById<Button>(R.id.dateFrom).setOnClickListener{
            val now = Calendar.getInstance()
            val currentYear: Int = now.get(Calendar.YEAR)
            val currentMonth: Int = now.get(Calendar.MONTH)
            val currentDay: Int = now.get(Calendar.DAY_OF_MONTH)
            val datePickerDialog = DatePickerDialog.newInstance(DatePickerDialog.OnDateSetListener { view2, year, monthOfYear, dayOfMonth ->
                yearFrom = year
                monthFrom = monthOfYear
                dayFrom = dayOfMonth
                view.findViewById<TextView>(R.id.dataFromText).text = "Data początkowa: " + yearFrom.toString() + "-" + monthFrom.toString() + "-" + dayFrom.toString()
            }, currentYear, currentMonth, currentDay)
            datePickerDialog.setTitle("Początek wydarzenia")
            datePickerDialog.accentColor = resources.getColor(R.color.colorPrimary)
            datePickerDialog.setOkText("Ustaw")
            datePickerDialog.setCancelText("Anuluj")
            datePickerDialog.show(requireFragmentManager(),"")

        }

        view.findViewById<Button>(R.id.dateTo).setOnClickListener{
            val now = Calendar.getInstance()
            val currentYear: Int = now.get(Calendar.YEAR)
            val currentMonth: Int = now.get(Calendar.MONTH)
            val currentDay: Int = now.get(Calendar.DAY_OF_MONTH)
            val datePickerDialog = DatePickerDialog.newInstance(DatePickerDialog.OnDateSetListener { view2, year, monthOfYear, dayOfMonth ->
                yearTo = year
                monthTo= monthOfYear
                dayTo = dayOfMonth
                view.findViewById<TextView>(R.id.dataToText).text = "Data końcowa: " + yearTo.toString() + "-" + monthTo.toString() + "-" + dayTo.toString()
            }, currentYear, currentMonth, currentDay)
            datePickerDialog.setTitle("Koniec wydarzenia")
            datePickerDialog.accentColor = resources.getColor(R.color.colorPrimary)
            datePickerDialog.setOkText("Ustaw")
            datePickerDialog.setCancelText("Anuluj")
            datePickerDialog.show(requireFragmentManager(),"")
        }

        view.findViewById<Button>(R.id.accept).setOnClickListener{
            if (dayFrom == -1 || monthFrom == -1 || yearFrom == -1 || dayTo == -1 || monthTo == -1 || yearTo == -1) {
                Toast.makeText(activity,"Ustaw obie daty!",Toast.LENGTH_SHORT).show();
            } else {
                //TODO: SPRAWDZ CZY DATA WCZESNIEJSZA JEST WCZESNIEJSZA
                Log.e("Day To: %s", dayTo.toString())
                Log.e("Month To: %s", monthTo.toString())
                Log.e("Year To: %s", yearTo.toString())
                Log.e("Day From: %s", dayFrom.toString())
                Log.e("Month From: %s", monthFrom.toString())
                Log.e("Year From: %s", yearFrom.toString())
            }
        }
    }

    companion object {
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            AddEventFragment().apply {}

        fun setLocation(location2: String) {
            location = location2
        }
    }


}
