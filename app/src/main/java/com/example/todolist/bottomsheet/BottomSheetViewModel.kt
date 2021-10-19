package com.example.todolist.bottomsheet

import android.app.AlarmManager
import android.app.Application
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.todolist.Task
import com.example.todolist.TaskDatabase
import com.example.todolist.receiver.AlarmReceiver
import com.example.todolist.repository.TaskRepository
import kotlinx.coroutines.*
import java.util.*

public class BottomSheetViewModel(application: Application) : AndroidViewModel(application) {

    private var day = 0
    private var month = 0
    private var year = 0
    private var hour = 0
    private var minute = 0
    private var taskId: Long = 0
    private lateinit var task2: Task
    var header2 = MutableLiveData<String>()
    var body2 = MutableLiveData<String>()
    private val _navigateToNote = MutableLiveData<Boolean>()
    public val navigateToNote: LiveData<Boolean>
        get() = _navigateToNote

    private val _navigateToTime = MutableLiveData<Boolean>()
    public val navigateToTime: LiveData<Boolean>
        get() = _navigateToTime

    private val _navigateBackToHomePage = MutableLiveData<Boolean>()
    public val navigateBackToHomePage: LiveData<Boolean>
        get() = _navigateBackToHomePage

    val job = Job();
    val scope = CoroutineScope(Dispatchers.Main + job)

    init {
        task2 = Task()
    }

    override fun onCleared() {
        super.onCleared()
        job.cancel()
    }

    fun navigateToNote() {
        _navigateToNote.value = true
    }

    fun finishedNavNote() {
        _navigateToNote.value = false
    }

    fun navigateToTime() {
        Log.i("hey", "haa")
        _navigateToTime.value = true
    }

    fun finishedNavTime() {
        _navigateToTime.value = false
    }

    private fun addNewTask(calendar: Calendar?) {

        if (header2.value.equals(null)) {
            showError()
        } else {
            task2.header = header2.value!!
            task2.body = body2.value
            if (calendar != null) {
                task2.timeInMillis = calendar.timeInMillis
            }
            scope.launch(Dispatchers.IO) {
                val taskIdd = repository.insertTask(task2)
                if (header2.value != null && calendar != null)
                    startTimer(calendar, taskIdd)
            }
        }

    }


    public fun addNew(calendar: Calendar?) {
        addNewTask(calendar)
    }

    private val repository = TaskRepository(TaskDatabase.getInstance(application).taskDatabaseDao)

    fun navigateBack2() {
        _navigateBackToHomePage.value = true
    }

    fun showError() {
        Toast.makeText(getApplication<Application>(), "No Text Entered", Toast.LENGTH_SHORT).show()
    }

    fun finishedNav2() {
        _navigateBackToHomePage.value = false
    }

    private fun startTimer(calendar: Calendar?, taskIdd: Long) {
        val alarmManager =
            getApplication<Application>().getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val intent = Intent(getApplication<Application>(), AlarmReceiver::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
        intent.putExtra("taskId", taskIdd)
        intent.putExtra("message", header2.value.toString())
        val pendingIntent = PendingIntent.getBroadcast(
            getApplication<Application>(),
            (Date().time / 1000L % Int.MAX_VALUE).toInt(),
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT
        )
        alarmManager.setExact(AlarmManager.RTC_WAKEUP, calendar!!.timeInMillis, pendingIntent)
    }
}