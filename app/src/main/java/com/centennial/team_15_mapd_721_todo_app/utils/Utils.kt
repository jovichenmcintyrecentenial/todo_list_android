import android.content.Context
import android.widget.EditText
import android.widget.Toast
import org.json.JSONException
import org.json.JSONObject

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
        fun showMessage(context: Context, message: String, longToast: Boolean = false) {
            val duration = if (longToast) Toast.LENGTH_LONG else Toast.LENGTH_SHORT
            Toast.makeText(context, message, duration).show()
        }

        fun isJsonValid(json: String): Boolean {
            try {
                JSONObject(json)
                return true
            } catch (ex: JSONException) {
                return false
            }
        }
    }

}