package com.example.todolist.home

import android.widget.TextView
import androidx.databinding.BindingAdapter
import com.example.todolist.Task

@BindingAdapter("updateText")
fun TextView.setHeader(task: Task) {
    text = task.header
}