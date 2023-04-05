package com.centennial.team_15_mapd_721_todo_app.repository

import UserRepository
import android.content.Context
import com.centennial.team_15_mapd_721_todo_app.models.Database
import com.centennial.team_15_mapd_721_todo_app.models.TaskModel
import kotlinx.coroutines.tasks.await

class TaskRepository {

    //defining database and live data object as companion objects
    companion object {
        val collection = "tasks"

        //function used to update and insert order data
        suspend fun insertUpdateData(context: Context, taskModel: TaskModel): Boolean {
            taskModel.idCreate()
            return try {
                taskModel.id?.let {
                    Database.getDB()!!
                        .collection(collection)
                        .document(it)
                        .set(taskModel)
                        .await()
                }
                return true // Return true if the insert/update was successful
            } catch (e: Exception) {
                e.printStackTrace()
                return false // Return false if an exception occurred
            }
        }

        //get all order from customer with specified id
        suspend fun getMyTask(context: Context): List<TaskModel>? {

            var quertSnapshot = Database.getDB()!!.collection(collection)
                .whereEqualTo("userId", UserRepository.user.value!!.id)
                .get().await()
            if(!quertSnapshot.isEmpty){
                val tasks = quertSnapshot.toObjects(TaskModel::class.java)
                return tasks
            }
            return null
        }


    }
}