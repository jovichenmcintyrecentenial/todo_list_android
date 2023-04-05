package com.centennial.team_15_mapd_721_todo_app.ui.task_details

import UserRepository
import android.content.Context
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.centennial.team_15_mapd_721_todo_app.models.TaskModel
import com.centennial.team_15_mapd_721_todo_app.repository.TaskRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class TaskViewModel: ViewModel() {

    val liveDataTaskCompleted: MutableLiveData<Boolean?> by lazy {
        MutableLiveData<Boolean?>()
    }

    fun addTask(context: Context, taskModel: TaskModel) {
        taskModel.userId = UserRepository.user.value!!.id
        CoroutineScope(Dispatchers.IO).launch {
            withContext(Dispatchers.IO) {
                liveDataTaskCompleted.postValue(TaskRepository.insertUpdateData(context, taskModel))
            }
            // The insert/update is completed at this point
        }
    }
}