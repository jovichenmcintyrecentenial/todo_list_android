package com.centennial.team_15_mapd_721_todo_app.models

import java.util.UUID

data class UserModel(
    var email: String? = null,
    var firstname: String? = null,
    var lastname: String? = null,
    var address: String? = null,
    var password: String? = null,
    var id:String? = null
){
    //defining a primary key field Id
    fun idCreate(){
        if(id == null){
            id = UUID.randomUUID().toString()
        }
    }
}

