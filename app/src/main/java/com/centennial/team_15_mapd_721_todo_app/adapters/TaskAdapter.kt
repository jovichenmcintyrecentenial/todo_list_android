package com.centennial.team_15_mapd_721_todo_app.adapters
import android.content.Context
import android.graphics.Paint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import com.centennial.team_15_mapd_721_todo_app.R
import com.centennial.team_15_mapd_721_todo_app.models.TaskModel
import com.centennial.team_15_mapd_721_todo_app.service.AlarmService
import java.text.SimpleDateFormat
import java.util.*


class TaskAdapter(private val items: MutableList<TaskModel>,private val onItemClicked: (TaskModel) -> Unit) : RecyclerView.Adapter<TaskAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val textView: TextView = view.findViewById(R.id.itemTextView)
        val dueDate: TextView = view.findViewById(R.id.dueDate)
        val image: ImageView = view.findViewById(R.id.image)
        val root = view.findViewById<LinearLayout>(R.id.root_view)

    }

    private lateinit var context:Context

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.list_item, parent, false)
        context = parent.context
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

        if (isDueDatePassed(item) && !item.isCompleted!!) {
            holder.dueDate.setTextColor(ContextCompat.getColor(context, android.R.color.holo_red_dark))
        }
        else{
            holder.dueDate.setTextColor(ContextCompat.getColor(context, android.R.color.darker_gray))
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

    fun deleteItem(position: Int) {
        items.removeAt(position)
        notifyItemRemoved(position)
    }

    fun completeItem(position: Int) {
        val task = items[position]
        if (task.dueDate != null) {
            if (task.isCompleted!!) {
                AlarmService.cancelAlarm(context, task)
            } else {
                if (!isDueDatePassed(task)) {
                    AlarmService.setAlarm(context, task)
                }
            }
        }
        notifyDataSetChanged()
    }

    fun isDueDatePassed(task: TaskModel): Boolean {
        if (task.dueDate == null) {
            // If the task does not have a due date, consider it not passed
            return false
        }

        val dueCalendar = Calendar.getInstance()
        dueCalendar.time = task.dueDate

        val nowCalendar = Calendar.getInstance()

        // Compare the due date with the current date and time using getTimeInMillis()
        return dueCalendar.getTimeInMillis() < nowCalendar.getTimeInMillis()
    }

}

open class SwipeToDeleteCallback(private val adapter: TaskAdapter) :
    ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT) {

    override fun onMove(
        recyclerView: RecyclerView,
        viewHolder: RecyclerView.ViewHolder,
        target: RecyclerView.ViewHolder
    ): Boolean {
        return false
    }

    override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
        val position = viewHolder.adapterPosition
        adapter.deleteItem(position)
    }
}