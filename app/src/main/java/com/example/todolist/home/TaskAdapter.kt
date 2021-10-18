package com.example.todolist.home

import android.graphics.Paint.*
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.compose.ui.graphics.Paint
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.todolist.Task
import com.example.todolist.TaskDatabase
import com.example.todolist.databinding.ListItemTaskBinding
import kotlinx.coroutines.*
import java.text.SimpleDateFormat

public class TaskAdapter(val clickListener: TaskListener) :
    ListAdapter<Task, TaskViewHolder>(TaskDiffUtilCallback()) {

    // this fixes the bug that caused recycler view to use old view holder data
    override fun onViewRecycled(holder: TaskViewHolder) {
        super.onViewRecycled(holder)
        holder.binding.textView.showStrikeThrough(false)
        holder.binding.textView2.showStrikeThrough(false)
        holder.binding.time.text= null
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TaskViewHolder {
        return TaskViewHolder.from(parent)
    }

    override fun onBindViewHolder(holder: TaskViewHolder, position: Int) {
        holder.bind(getItem(position), clickListener)
    }}

class TaskDiffUtilCallback : DiffUtil.ItemCallback<Task>() {
    override fun areItemsTheSame(oldItem: Task, newItem: Task): Boolean {
        return oldItem._id.toInt() == newItem._id.toInt()
    }

    override fun areContentsTheSame(oldItem: Task, newItem: Task): Boolean {
        return oldItem._id.toInt() == newItem._id.toInt()
    }
}

lateinit var database: TaskDatabase

val job = Job()
val scope = CoroutineScope(Dispatchers.Main + job)

class TaskViewHolder private constructor(val binding: ListItemTaskBinding) :
    RecyclerView.ViewHolder(binding.root) {
    fun bind(item: Task, clickListener: TaskListener) {
        binding.task = item

        binding.taskDoneButton.setOnClickListener {
            binding.textView.showStrikeThrough(true)
            binding.textView2.showStrikeThrough(true)
            deleteTaskMethod(item)
        }

        binding.clickListener = clickListener
    }




    private fun deleteTaskMethod(item: Task) {
        scope.launch {
            deleteTask(item)
        }
    }

    public suspend fun deleteTask(task: Task) {
        withContext(Dispatchers.IO) {
            database.taskDatabaseDao.delete(task)
        }
    }

    companion object {
        fun from(parent: ViewGroup): TaskViewHolder {
            val inflater = LayoutInflater.from(parent.context)
            val binding = ListItemTaskBinding.inflate(inflater, parent, false)
            database = TaskDatabase.getInstance(parent.context)
            return TaskViewHolder(binding)
        }
    }
}

private fun TextView.showStrikeThrough(show: Boolean) {
    paintFlags =
        if (show) paintFlags or STRIKE_THRU_TEXT_FLAG
        else paintFlags and STRIKE_THRU_TEXT_FLAG.inv()
}

class TaskListener(val ClickListener: (taskid: Long) -> Unit) {
    fun onCLick(task: Task) = ClickListener(task._id)
}
