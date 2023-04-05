package com.centennial.team_15_mapd_721_todo_app.ui.task_details

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.EditText
import com.centennial.team_15_mapd_721_todo_app.R
import com.centennial.team_15_mapd_721_todo_app.databinding.ActivityLoginBinding
import com.centennial.team_15_mapd_721_todo_app.databinding.ActivityTaskDetailsBinding
import com.google.type.DateTime
import java.text.SimpleDateFormat
import java.util.*

class TaskDetailsActivity : AppCompatActivity() {
    private lateinit var binding: ActivityTaskDetailsBinding
    private var date:Date? = null
    private val dateFormat = SimpleDateFormat("MMMM dd, yyyy", Locale.getDefault())
    private val timeFormat = SimpleDateFormat("hh:mm a", Locale.getDefault())


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityTaskDetailsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setCurrentDateAndTime()
        binding.hasDueDateSwitch.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                binding.dateTimelayout.visibility = View.VISIBLE
            } else {
                binding.dateTimelayout.visibility = View.GONE
            }
        }
    }

    fun setCurrentDateAndTime(){

        date = Date()

        val currentDateString = dateFormat.format(date)
        val currentTimeString = timeFormat.format(date)

        val dateEditText = binding.dateEditText
        val timeEditText = binding.timeEditText

        dateEditText.setText(currentDateString)
        timeEditText.setText(currentTimeString)
    }

    fun showDatePickerDialog(view: View) {
        val editText = view as EditText
        val calendar = Calendar.getInstance()
        calendar.time = date
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val day = calendar.get(Calendar.DAY_OF_MONTH)

        val datePickerDialog = DatePickerDialog(
            this,
            { _, selectedYear, selectedMonth, selectedDay ->
                calendar.set(Calendar.YEAR, selectedYear)
                calendar.set(Calendar.MONTH, selectedMonth)
                calendar.set(Calendar.DAY_OF_MONTH, selectedDay)
                date = calendar.time

                val currentDateString = dateFormat.format(date)

                editText.setText(currentDateString)
            },
            year,
            month,
            day
        )

        datePickerDialog.show()
    }

    fun showTimePickerDialog(view: View) {
        val editText = view as EditText
        val calendar = Calendar.getInstance()
        calendar.time = date
        val hour = calendar.get(Calendar.HOUR_OF_DAY)
        val minute = calendar.get(Calendar.MINUTE)

        val timePickerDialog = TimePickerDialog(
            this,
            { _, selectedHour, selectedMinute ->
                calendar.set(Calendar.HOUR_OF_DAY, selectedHour)
                calendar.set(Calendar.MINUTE, selectedMinute)
                date = calendar.time
                val currentTimeString = timeFormat.format(date)
                editText.setText(currentTimeString)
            },
            hour,
            minute,
            false
        )

        timePickerDialog.show()
    }}