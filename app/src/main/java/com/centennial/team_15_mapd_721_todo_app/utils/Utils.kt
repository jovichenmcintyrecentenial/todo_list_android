import android.content.Context
import android.widget.EditText
import android.widget.Toast

class Utils {
    companion object {

        //check if edittext is empty or not
        fun emptyValidation(edittext: EditText, error: String): Boolean {
            if (edittext.text.trim().isEmpty()) {
                throw UserInputException(error)
            }
            return false
        }

        //use to display toast messages
        fun showMessage(context:Context,message:String){
            Toast.makeText(context,message, Toast.LENGTH_SHORT).show()
        }
    }

}