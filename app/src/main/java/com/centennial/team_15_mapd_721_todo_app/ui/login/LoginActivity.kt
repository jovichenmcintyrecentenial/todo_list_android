package com.centennial.team_15_mapd_721_todo_app.ui.login

import LoginViewModel
import UserInputException
import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import com.centennial.team_15_mapd_721_todo_app.R
import com.centennial.team_15_mapd_721_todo_app.databinding.ActivityLoginBinding
import com.centennial.team_15_mapd_721_todo_app.models.Database
import com.centennial.team_15_mapd_721_todo_app.models.UserModel
import com.centennial.team_15_mapd_721_todo_app.ui.signup.SignUpActivity
import androidx.lifecycle.Observer
import com.centennial.team_15_mapd_721_todo_app.ui.main.MainActivity


class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding

    lateinit var loginViewModel: LoginViewModel
    lateinit var loginObserver:Observer<UserModel?>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Database.initDatabase(applicationContext)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        loginViewModel = ViewModelProvider(this).get(modelClass = LoginViewModel::class.java)

        loginObserver = Observer<UserModel?> { userModel ->

            if(userModel != null) {
                val sharedPreference =  getSharedPreferences("STORE",Context.MODE_PRIVATE)

                var editor = sharedPreference.edit()
                editor.putString("username", userModel.firstname)
                editor.apply()

                Toast.makeText(this,"Login Successful",Toast.LENGTH_LONG).show()
                var intent = Intent(this, MainActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
                startActivity(intent)

            }
            else{
                Toast.makeText(this,"Invalid username or password",Toast.LENGTH_LONG).show()
            }
        }

        loginViewModel.liveUserData.observe(this, loginObserver)
    }

    override fun onDestroy() {
        super.onDestroy()
        loginViewModel.liveUserData.removeObserver(loginObserver)

    }



    fun onCreateAccountClicked(view: View) {

        val intent = Intent(this, SignUpActivity::class.java)
        startActivity(intent)
    }

    fun onLogin(view: View) {
        try{
            if(isDataValid()) {
                loginViewModel.getUser(
                    this,
                    binding.editTextEmail.text.toString(),
                    binding.editTextPassword.text.toString()
                )
            }
        }
        //catch  and display user input exception
        catch (e: UserInputException) {
            //display exception message
            Utils.showMessage(this,e.message.toString())
        }
    }



    private fun isDataValid(): Boolean {

        Utils.emptyValidation(binding.editTextEmail, "Enter a username")
        Utils.emptyValidation(binding.editTextPassword, "Enter a password")

        return true
    }
}