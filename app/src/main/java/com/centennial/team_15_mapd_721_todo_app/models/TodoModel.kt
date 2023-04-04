package com.centennial.team_15_mapd_721_todo_app.models

import java.util.*

data class TodoModel(

    var name: String? = null,
    var isCompleted: String? = null,
    var dueDate: String? = null,
    var note: String? = null,
    var id:String? = null
){
    //defining a primary key field Id
    fun idCreate(){
        if(id == null){
            id = UUID.randomUUID().toString()
        }
    }
}

