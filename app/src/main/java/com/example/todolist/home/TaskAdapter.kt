package com.example.todolist.home

import android.view.LayoutInflater
import android.view.ViewGroup
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

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TaskViewHolder {
        return TaskViewHolder.from(parent)
    }

    override fun onBindViewHolder(holder: TaskViewHolder, position: Int) {
        holder.bind(getItem(position), clickListener)

    }

}

class TaskDiffUtilCallback : DiffUtil.ItemCallback<Task>() {
    override fun areItemsTheSame(oldItem: Task, newItem: Task): Boolean {
        return oldItem === newItem
    }

    override fun areContentsTheSame(oldItem: Task, newItem: Task): Boolean {
        return oldItem == newItem
    }
}

lateinit var database: TaskDatabase

val job = Job()
val scope = CoroutineScope(Dispatchers.Main + job)

class TaskViewHolder private constructor(val binding: ListItemTaskBinding) :
    RecyclerView.ViewHolder(binding.root) {
    fun bind(item: Task, clickListener: TaskListener) {
        binding.task = item
        val simpleDateFormat = SimpleDateFormat(" dd/MM/yyyy")
        val seconds = item.timeInMillis
        val s = seconds % 60
        val m = seconds / 60 % 60
        val h = seconds / (60 * 60) % 24
        val ssd = String.format("%d:%02d:%02d", h, m, s)
        val dateString: String = simpleDateFormat.format(item.timeInMillis)



//        binding.textView3.text = dateString
//        binding.textView4.text = ssd
        binding.clickListener = clickListener
//        binding.button.setOnClickListener {
//            deleteTaskMethod(item)
//        }
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

class TaskListener(val ClickListener: (taskid: Long) -> Unit) {
    fun onCLick(task: Task) = ClickListener(task._id)
}
