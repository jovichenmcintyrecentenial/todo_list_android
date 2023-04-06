package com.centennial.team_15_mapd_721_todo_app.ui.main

import android.content.Context
import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.centennial.team_15_mapd_721_todo_app.models.TaskModel
import com.centennial.team_15_mapd_721_todo_app.repository.TaskRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MainViewModel : ViewModel() {

    val liveTaskListData: MutableLiveData<List<TaskModel>?> by lazy {
        MutableLiveData<List<TaskModel>?>()
    }

    fun getTasks(context: Context){

        CoroutineScope(Dispatchers.IO).launch {

            val data = TaskRepository.getMyTask(context)
            if(data != null) {
                liveTaskListData.postValue(data)
            }
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