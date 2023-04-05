package com.centennial.team_15_mapd_721_todo_app.ui.main

import android.content.Intent
import android.os.Bundle
import com.google.android.material.snackbar.Snackbar
import androidx.appcompat.app.AppCompatActivity
import android.view.Menu
import android.view.MenuItem
import com.centennial.team_15_mapd_721_todo_app.R
import com.centennial.team_15_mapd_721_todo_app.databinding.ActivityMainBinding
import com.centennial.team_15_mapd_721_todo_app.service.AlarmService
import com.centennial.team_15_mapd_721_todo_app.ui.signup.SignUpActivity
import com.centennial.team_15_mapd_721_todo_app.ui.task_details.TaskDetailsActivity

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbar)

        binding.fab.setOnClickListener { view ->
            val intent = Intent(this, TaskDetailsActivity::class.java)
            startActivity(intent)
        }

        AlarmService.initialize(this)
//        AlarmService.notifications!!.showNotification("Titsle","Message",null)

    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_settings -> true
            else -> super.onOptionsItemSelected(item)
        }
    }
}