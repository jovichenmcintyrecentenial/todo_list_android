package com.centennial.team_15_mapd_721_todo_app.ui.main

import android.content.Context
import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.android.volley.Response
import com.centennial.team_15_mapd_721_todo_app.api.ApiClient
import com.centennial.team_15_mapd_721_todo_app.models.SpeechInterpret
import com.centennial.team_15_mapd_721_todo_app.models.TaskModel
import com.centennial.team_15_mapd_721_todo_app.repository.TaskRepository
import com.google.gson.Gson
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

class MainViewModel : ViewModel() {

    val liveTaskListData: MutableLiveData<List<TaskModel>?> by lazy {
        MutableLiveData<List<TaskModel>?>()
    }
    val liveSpeechInterpret: MutableLiveData<SpeechInterpret?> by lazy {
        MutableLiveData<SpeechInterpret?>()
    }

    fun getTasks(context: Context){

        CoroutineScope(Dispatchers.IO).launch {

            val data = TaskRepository.getMyTask(context)
            if(data != null) {
                liveTaskListData.postValue(data)
            }
        }
    }

    fun interpretSpeech(context: Context, speech:String){

        CoroutineScope(Dispatchers.IO).launch {
            val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
            val date = Date()
            val dateString = dateFormat.format(date)
            ApiClient.interpretTaskInfo(
                context,
                speech,
                dateString,
                { response ->
                    Log.d("Interpreted Speech", response)
                    if(Utils.isJsonValid(response)){

                        val speechInterpret = Gson().fromJson(response.trim(), SpeechInterpret::class.java)
                        liveSpeechInterpret.postValue(speechInterpret)
                    }
                },
                { error ->

                })
        }
    }

    fun deleteTask(taskModel:TaskModel){

        CoroutineScope(Dispatchers.IO).launch {

            val data = TaskRepository.deleteItem(taskModel = taskModel) { result ->
                if (result) {
                    Log.d("DeleteItem", "Item deleted successfully")
                } else {
                    Log.e("DeleteItem", "Failed to delete item")
                }
            }
        }
    }
    fun makeAsCompleted(context:Context, taskModel:TaskModel){

        CoroutineScope(Dispatchers.IO).launch {
            taskModel.isCompleted = !taskModel.isCompleted!!
            TaskRepository.insertUpdateData(context,taskModel)

        }
    }
}