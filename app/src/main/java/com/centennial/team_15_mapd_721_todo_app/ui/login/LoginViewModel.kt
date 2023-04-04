import android.content.Context
import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.centennial.team_15_mapd_721_todo_app.models.UserModel
import com.google.firebase.FirebaseApp
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


class LoginViewModel: ViewModel() {

    val liveUserData: MutableLiveData<UserModel?> by lazy {
        MutableLiveData<UserModel?>()
    }

    fun getUser(context: Context, username:String, password:String){

        CoroutineScope(Dispatchers.IO).launch {
            liveUserData.postValue( UserRepository.passwordCheck(context, username, password))
        }
    }


}