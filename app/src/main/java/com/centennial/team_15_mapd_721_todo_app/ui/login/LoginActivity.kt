package com.centennial.team_15_mapd_721_todo_app.ui.login

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import com.centennial.team_15_mapd_721_todo_app.R
import com.centennial.team_15_mapd_721_todo_app.models.Database
import com.centennial.team_15_mapd_721_todo_app.ui.signup.SignUpActivity

class LoginActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Database.initDatabase(applicationContext)

        setContentView(R.layout.activity_login)
    }

    fun onCreateAccountClicked(view: View) {

        val intent = Intent(this, SignUpActivity::class.java)
        startActivity(intent)
    }
}