package com.centennial.team_15_mapd_721_todo_app.ui.signup

import UserRepository
import android.content.Context
import androidx.lifecycle.ViewModel
import com.centennial.team_15_mapd_721_todo_app.models.UserModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class SignUpViewModel: ViewModel()  {

    fun register(context: Context, userModel: UserModel) {
        CoroutineScope(Dispatchers.IO).launch {
            UserRepository.insertData(context, userModel)
        }
    }
}