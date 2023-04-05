package com.centennial.team_15_mapd_721_todo_app.models

import java.util.*

data class TaskModel(

    var name: String? = null,
    var note: String? = null,
    var dueDate: Date? = null,
    var isCompleted: String? = null,
    var id:String? = null,
    var userId: String? = null

){
    //defining a primary key field Id
    fun idCreate(){
        if(id == null){
            id = UUID.randomUUID().toString()
        }
    }
}

