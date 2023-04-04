package com.centennial.team_15_mapd_721_todo_app.ui.login

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.centennial.team_15_mapd_721_todo_app.R
import com.centennial.team_15_mapd_721_todo_app.models.Database

class LoginActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Database.initDatabase(applicationContext)

        setContentView(R.layout.activity_login)
    }
}