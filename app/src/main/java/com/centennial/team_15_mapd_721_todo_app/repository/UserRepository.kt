

import android.content.Context
import com.centennial.team_15_mapd_721_todo_app.models.Database
import com.centennial.team_15_mapd_721_todo_app.models.UserModel
import kotlinx.coroutines.tasks.await

class UserRepository {

    //defining database and live data object as companion objects
    companion object {
        private const val collection = "users"

        //insert user data
        suspend fun insertData(context: Context, customer:UserModel)  {

                Database.getDB()!!
                    .collection(collection)
                    .document(customer.email!!)
                    .set(customer)
                    .await()
        }


        //update user data
        suspend  fun update(context: Context, customer:UserModel) {

            Database.getDB()!!
                .collection(collection)
                .document(customer.email!!)
                .set(customer)
                .await()

        }
        //check user login credentials
        suspend fun passwordCheck(context: Context, username: String, password:String):UserModel? {

            var quertSnapshot = Database.getDB()!!.collection(collection)
                .whereEqualTo("password", password)
                .whereEqualTo("email",username)
                .get().await()
            if(!quertSnapshot.isEmpty){
                 var customer = quertSnapshot.toObjects(UserModel::class.java)[0]
                return customer
            }

            return null
        }

    }
}