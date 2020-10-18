package com.example.todolist.repository

import android.app.Application
import android.content.Context
import androidx.lifecycle.LiveData
import androidx.room.Dao
import com.example.todolist.Task
import com.example.todolist.TaskDatabase
import com.example.todolist.TaskDatabaseDao
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext

public class TaskRepository() {
    var fapplication: Application? = null

    constructor(application: Application) : this() {
        fapplication = application
        val databaseRef = TaskDatabase.getInstance(application)
        database = databaseRef.taskDatabaseDao
    }

    constructor(databaseDao: TaskDatabaseDao) : this() {
        database = databaseDao
    }

    private lateinit var database: TaskDatabaseDao

    public fun searchTask(query: String?): LiveData<List<Task>> {
        return database.getSearchResult(query)
    }

    public fun filterAccToDate(filter: String) {
        when (filter) {
            "Today" -> sortTod()
            "Tomorrow" -> sortTom()
            "Later" -> sortLater()
            "All" -> sortAll()
        }
    }


    private fun sortAll() {


    }

    private fun sortLater() {
    }

    private fun sortTom() {
    }

    private fun sortTod() {
    }

    public fun getAllTasks(): LiveData<List<Task>> {
        return database.getAllTasks()
    }


    public suspend fun getLatestTask(): Task {
        return withContext(Dispatchers.IO) {
            database.getLatestTask()
        }
    }


    public suspend fun insertTask(task: Task): Long {
            return database.insert(task)
    }

    suspend fun delete(task: Task?) {
        withContext(Dispatchers.IO) {
            if (task != null) {
                database.delete(task)
            }
        }
    }

    suspend fun getTask(taskId: Long): Task? {
        return withContext(Dispatchers.IO) {
            val task = database.get(taskId)
            task
        }
    }

    suspend fun update(value: Task?) {

        withContext(Dispatchers.IO) {
            if (value != null) {
                database.update(value)
            }
        }
    }
}