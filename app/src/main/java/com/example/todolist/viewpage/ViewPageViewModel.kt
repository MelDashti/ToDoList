package com.example.todolist.viewpage

import android.app.Application
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.todolist.Task
import com.example.todolist.TaskDatabase
import com.example.todolist.repository.TaskRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.*
import javax.inject.Inject

@HiltViewModel
class ViewPageViewModel @Inject constructor(
    private val repository: TaskRepository
) : ViewModel() {

    private val _navigateBackToHome = MutableLiveData<Boolean>()
    public val navigateBackToHome: LiveData<Boolean>
        get() = _navigateBackToHome


    var task = MutableLiveData<Task?>()

    init {

        task.value = Task()

    }

    public fun fetchTaskInfo(taskId: Long) {
        viewModelScope.launch {
            task.value = repository.getTask(taskId)
            header.value = task.value?.header
            body.value = task.value?.body
        }
    }

    public fun deleteTask() {
        viewModelScope.launch {

            repository.delete(task.value)
        }
        _navigateBackToHome.value = true
    }

    var header = MutableLiveData<String>()
    var body = MutableLiveData<String>()

    public fun updateTask() {
        viewModelScope.launch {
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
    }
}
