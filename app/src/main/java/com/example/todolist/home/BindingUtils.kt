package com.example.todolist.home

import android.opengl.Visibility
import android.view.View
import android.widget.TextView
import androidx.databinding.BindingAdapter
import com.example.todolist.Task
import java.time.format.DateTimeFormatter
import java.util.*

@BindingAdapter("updateHeader")
fun TextView.setHeader(task: Task) {
    text = task.header
}

@BindingAdapter("updateText")
fun TextView.setText(task: Task) {
    if (task.body != null ) {
        text = task.body
        visibility = View.VISIBLE
    } else visibility = View.GONE
}

@BindingAdapter("addTime")
fun TextView.addTime(task: Task) {
    val timeInMillis = task.timeInMillis
    if (timeInMillis != 0L) {
        val cl: Calendar = Calendar.getInstance()
        cl.timeInMillis = timeInMillis //here your time in miliseconds
        val date = "" + cl.get(Calendar.DAY_OF_MONTH).toString() + ":" + cl.get(Calendar.MONTH)
            .toString()
        val time = "" + cl.get(Calendar.HOUR_OF_DAY).toString() + ":" + cl.get(Calendar.MINUTE)
            .toString()
        text = time
    }
}
