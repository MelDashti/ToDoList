package com.example.todolist.viewpage

import android.app.Application
import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

public class ViewPageViewModelFactory(
    private val application: Application,
    private val taskId: Long
) : ViewModelProvider.Factory {
    @Suppress("unchecked_cast")
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ViewPageViewModel::class.java)) {
            return ViewPageViewModel(application, taskId) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}