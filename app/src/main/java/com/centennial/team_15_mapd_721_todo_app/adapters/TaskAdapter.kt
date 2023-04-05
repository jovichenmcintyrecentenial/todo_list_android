package com.centennial.team_15_mapd_721_todo_app.adapters
import android.graphics.Paint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.centennial.team_15_mapd_721_todo_app.R
import com.centennial.team_15_mapd_721_todo_app.models.TaskModel
import java.text.SimpleDateFormat
import java.util.*


class TaskAdapter(private val items: List<TaskModel>,private val onItemClicked: (TaskModel) -> Unit) : RecyclerView.Adapter<TaskAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val textView: TextView = view.findViewById(R.id.itemTextView)
        val dueDate: TextView = view.findViewById(R.id.dueDate)
        val image: ImageView = view.findViewById(R.id.image)
        val root = view.findViewById<LinearLayout>(R.id.root_view)

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.list_item, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = items[position]
        holder.root.setOnClickListener {
            onItemClicked(item)
        }
        if(item!!.isCompleted!!){
            holder.image.setImageResource(R.drawable.checked)
            holder.textView.paintFlags = holder.textView.paintFlags or Paint.STRIKE_THRU_TEXT_FLAG

        }
        else {
            holder.image.setImageResource(R.drawable.unchecked)
            holder.textView.paintFlags = holder.textView.paintFlags and Paint.STRIKE_THRU_TEXT_FLAG.inv()

        }

        if(item.dueDate != null){
            val calendar = Calendar.getInstance()
            val currentWeek = calendar.get(Calendar.WEEK_OF_YEAR)
            val today = calendar.get(Calendar.DAY_OF_YEAR)

            val taskCalendar = Calendar.getInstance()
            taskCalendar.time = item.dueDate ?: Date()

            val taskWeek = taskCalendar.get(Calendar.WEEK_OF_YEAR)
            val taskDay = taskCalendar.get(Calendar.DAY_OF_YEAR)

            val dateFormat = if (currentWeek == taskWeek) {
                if (today == taskDay) {
                    SimpleDateFormat("'Today at' hh:mm a", Locale.getDefault())
                } else {
                    SimpleDateFormat("EEEE 'at' hh:mm a", Locale.getDefault())
                }
            } else {
                SimpleDateFormat("EEEE, MMMM d, yyyy 'at' hh:mm a", Locale.getDefault())
            }

            holder.dueDate.text = dateFormat.format(item.dueDate!!)
        }
        else{
            holder.dueDate.text = "No Date"
        }

        holder.textView.text = item.name
    }

    override fun getItemCount(): Int = items.size
}