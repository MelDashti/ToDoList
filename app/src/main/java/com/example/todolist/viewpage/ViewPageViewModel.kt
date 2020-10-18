package com.example.todolist.viewpage

import android.app.Application
import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.todolist.Task
import com.example.todolist.repository.TaskRepository
import kotlinx.coroutines.*

class ViewPageViewModel(val application: Application, val taskId: Long) : ViewModel() {

    val job = Job()
    val uiScope = CoroutineScope(Dispatchers.Main + job);
    private val _navigateBackToHome = MutableLiveData<Boolean>()
    public val navigateBackToHome: LiveData<Boolean>
        get() = _navigateBackToHome


    var task = MutableLiveData<Task?>()

    init {
        initializeSelectedTask()
    }

    private val repository = TaskRepository(application)

    private fun initializeSelectedTask() {
        uiScope.launch {
            task.value = repository.getTask(taskId)
            header.value = task.value?.header
            body.value = task.value?.body
        }
    }

    public fun deleteTask() {
        uiScope.launch {

            repository.delete(task.value)
        }
        _navigateBackToHome.value = true }

    var header = MutableLiveData<String>()
    var body = MutableLiveData<String>()

    public fun updateTask() {
        uiScope.launch {
            task.value!!.header = header.value as String
            task.value!!.body = body.value as String
            repository.update(task.value)
        }
        _navigateBackToHome.value = true
    }


    fun navigateBack() {
        _navigateBackToHome.value = true
    }

    fun finishedNav() {
        _navigateBackToHome.value = false
    }

    init {
    }

    override fun onCleared() {
        super.onCleared()
        job.cancel()
    }
}
