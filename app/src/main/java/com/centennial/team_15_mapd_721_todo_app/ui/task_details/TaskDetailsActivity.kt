package com.centennial.team_15_mapd_721_todo_app.ui.task_details

import UserInputException
import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Context
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.EditText
import com.centennial.team_15_mapd_721_todo_app.databinding.ActivityTaskDetailsBinding
import java.text.SimpleDateFormat
import java.util.*
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.centennial.team_15_mapd_721_todo_app.models.MyConstants
import com.centennial.team_15_mapd_721_todo_app.models.MyConstants.Companion.SAVEDDATA
import com.centennial.team_15_mapd_721_todo_app.models.TaskModel
import com.centennial.team_15_mapd_721_todo_app.service.AlarmService
import com.google.gson.Gson

class TaskDetailsActivity : AppCompatActivity() {
    private lateinit var binding: ActivityTaskDetailsBinding
    private var date:Date? = null
    private val dateFormat = SimpleDateFormat("MMMM dd, yyyy", Locale.getDefault())
    private val timeFormat = SimpleDateFormat("hh:mm a", Locale.getDefault())
    private lateinit var taskViewModel: TaskViewModel
    private var taskModel: TaskModel? = null
    private val gson:Gson = Gson()
    private var shouldSave:Boolean = true
    private lateinit var sharedPreferences:SharedPreferences


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

        taskViewModel = ViewModelProvider(this).get(modelClass = TaskViewModel::class.java)

        val addTaskObserver = Observer<Boolean?> { boolean ->
            if (boolean == true) {
                sharedPreferences.edit().remove(SAVEDDATA).apply()
                shouldSave = false
                finish()

            } else {
                Utils.showMessage(this,"Unable to add task")
            }
        }
        sharedPreferences = getSharedPreferences(MyConstants.SHAREDPREFENCENAME ,Context.MODE_PRIVATE)

        taskViewModel.liveDataTaskCompleted.observe(this, addTaskObserver)

        // Check if the intent contains a task extra
        if(intent.hasExtra("task")){
            shouldSave = false
            taskModel = Gson().fromJson(intent.getStringExtra("task"),TaskModel::class.java)
        }
        // If the intent doesn't contain a task extra, check if there is saved data in SharedPreferences
        else if(sharedPreferences.contains(SAVEDDATA)){
            val taskJson = sharedPreferences.getString(SAVEDDATA, null)
            if(taskJson != null) {
                taskModel = Gson().fromJson(taskJson, TaskModel::class.java)
            }
        }
        populateUIWithData()
    }

    private fun populateUIWithData(){
        if(taskModel == null) return
        binding.editTaskName.setText(taskModel!!.name)
        binding.editTaskDetails.setText(taskModel!!.note)
        binding.isCompleted.isChecked = taskModel!!.isCompleted!!
        binding.hasDueDateSwitch.isChecked = taskModel!!.dueDate != null
        if(!shouldSave)
            binding.actionButton.text = "Update Task"

        if(taskModel!!.dueDate != null){
            setCurrentDateAndTime(taskModel!!.dueDate)
        }
    }



    private fun setCurrentDateAndTime(tempDate:Date? = null){

        if(tempDate == null) {
            date = Date()
        }
        else{
            date = tempDate!!
        }

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
    }

    fun onSubmit(view: View) {
        try{
            if(isDataValid()){

                //store values in string varibles
                val taskName = binding.editTaskName.text.toString()
                val taskDetails = binding.editTaskDetails.text.toString()

                if(taskModel == null) {
                    //create new task model
                    taskModel = TaskModel(
                        taskName,
                        taskDetails,
                        if (binding.hasDueDateSwitch.isChecked) date else null,
                        binding.isCompleted.isChecked
                    )
                    taskModel!!.idCreate()
                }
                else{
                    taskModel!!.name = taskName
                    taskModel!!.note = taskDetails
                    taskModel!!.dueDate = if (binding.hasDueDateSwitch.isChecked) date else null
                    taskModel!!.isCompleted = binding.isCompleted.isChecked
                }

                if(taskModel!!.dueDate != null) {
                    AlarmService.setAlarm(this, taskModel!!,)
                }
                //use view model to insert data in database
                taskViewModel.addTask(this,taskModel!!)
            }
        }
        //catch  and display user input exception
        catch (e: UserInputException) {
            //display exception message
            Utils.showMessage(this, e.message.toString())
        }
    }

    private fun isDataValid(): Boolean {
        Utils.emptyValidation(binding.editTaskName, "Please enter a task name")

        val currentDate = Calendar.getInstance().time

        if(binding.dateTimelayout.visibility == View.VISIBLE) {
            if (date != null && date!!.before(currentDate)) {
                throw UserInputException("Please enter date that hasn't already passed")
            }
        }
        return true
    }

    override fun onDestroy() {
        super.onDestroy()
        if(!shouldSave) return
        val task = TaskModel(
            binding.editTaskName.text.toString(),
            binding.editTaskDetails.text.toString(),
            if (binding.hasDueDateSwitch.isChecked) date else null,
            binding.isCompleted.isChecked
        )
        sharedPreferences.edit().putString(SAVEDDATA,  gson.toJson(task)).apply()
    }
}