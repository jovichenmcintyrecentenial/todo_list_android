package com.centennial.team_15_mapd_721_todo_app.ui.main

import android.content.Context
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
}