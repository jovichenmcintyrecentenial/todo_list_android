package com.centennial.team_15_mapd_721_todo_app.models

import com.google.firebase.firestore.ServerTimestamp
import java.util.*

data class TaskModel(

    var name: String? = null,
    var note: String? = null,
    var dueDate: Date? = null,
    var isCompleted: Boolean? = null,
    var id:String? = null,
    var userId: String? = null,
    @ServerTimestamp var createdAt: Date? = null
){
    //defining a primary key field Id
    fun idCreate(){
        if(id == null){
            id = UUID.randomUUID().toString()
        }
    }
}

