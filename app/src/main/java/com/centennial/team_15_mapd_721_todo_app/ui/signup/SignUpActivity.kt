package com.centennial.team_15_mapd_721_todo_app.ui.signup

import UserInputException
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import com.centennial.team_15_mapd_721_todo_app.R
import com.centennial.team_15_mapd_721_todo_app.databinding.ActivitySignUpBinding
import com.centennial.team_15_mapd_721_todo_app.models.UserModel

class SignUpActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySignUpBinding
    lateinit var signUpViewModel: SignUpViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignUpBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //connect to view model
        signUpViewModel = ViewModelProvider(this).get(modelClass = SignUpViewModel::class.java)
    }

    fun onSubmit(view: View) {
        try{
            if(isDataValid()){

                //store edittext values in string varibles
                val firstName = binding.editTextFirstName.text.toString()
                val lastName = binding.editTextLastName.text.toString()
                val email = binding.editTextEmail.text.toString()
                val password = binding.editTextPassword.text.toString()

                //create new user model
                val userModel = UserModel(
                    firstName,
                    lastName,
                    email,
                    password
                )
                userModel.idCreate()

                //use view model to insert data in database
                signUpViewModel.register(this,userModel)

                //display success and pop screen
                Toast.makeText(this,"Successfully register", Toast.LENGTH_LONG).show()
                finish()

            }
        }
        //catch  and display user input exception
        catch (e: UserInputException) {
            //display exception message
            Utils.showMessage(this, e.message.toString())
        }
    }

    private fun isDataValid(): Boolean {
        Utils.emptyValidation(binding.editTextFirstName, "Please enter a firstname")
        Utils.emptyValidation(binding.editTextLastName, "Please enter a lastname")
        Utils.emptyValidation(binding.editTextEmail, "Please enter a email")
        Utils.emptyValidation(binding.editTextPassword, "Please enter a password")
        Utils.emptyValidation(binding.editRePassword, "Please re-enter password")

        //password check if match
        if(binding.editRePassword.text.toString() != binding.editTextPassword.text.toString()){
            throw UserInputException("Password doesn't match, please try again.")
        }

        return true
    }
}