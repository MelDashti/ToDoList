package com.example.todolist

import android.app.AlarmManager
import android.app.Application
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.widget.Toast
import androidx.lifecycle.*
import com.example.todolist.home.FilterType
import com.example.todolist.receiver.AlarmReceiver
import com.example.todolist.repository.TaskRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import java.util.*
import javax.inject.Inject

@HiltViewModel
class SharedViewModel @Inject constructor(
    private val repository: TaskRepository,
    private val application: Application
) : ViewModel() {

    private var task2: Task
    var header2 = MutableLiveData<String>()
    var body2 = MutableLiveData<String>()
    private val _navigateToNote = MutableLiveData<Boolean>()
    val navigateToNote: LiveData<Boolean>
        get() = _navigateToNote

    private val _navigateToTime = MutableLiveData<Boolean>()
    public val navigateToTime: LiveData<Boolean>
        get() = _navigateToTime

    private val _navigateBackToHomePage = MutableLiveData<Boolean>()
    val navigateBackToHomePage: LiveData<Boolean>
        get() = _navigateBackToHomePage

    private val job = Job();
    private val scope = CoroutineScope(Dispatchers.Main + job)

    private val _navigateBackToHome = MutableLiveData<Boolean>()
    val navigateBackToHome: LiveData<Boolean>
        get() = _navigateBackToHome

    private val _navigateToDatePicker = MutableLiveData<Boolean>()
    val navigateToDatePicker: LiveData<Boolean>
        get() = _navigateToDatePicker

    var task = MutableLiveData<Task?>()
    private var currentFiltering = MutableLiveData<FilterType>(FilterType.ACTIVE_TASKS)

    //this will update your list
    private val _forceUpdate = MutableLiveData<Boolean>(false)
    private val _tasks = repository.getAllTasks()
    val tasks: LiveData<List<Task>> = _tasks
    private val _navigateToBottomSheet = MutableLiveData<Boolean>()
    val navigateToBottomSheet: LiveData<Boolean> = _navigateToBottomSheet
    private val _query = MutableLiveData<String>()
    private val _startSearch = MutableLiveData<Boolean>()
    val startSearch: LiveData<Boolean> = _startSearch


    init {
        task.value = Task()
        task2 = Task()
        _startSearch.value = false
        searchNow("")
        //        initializeLatestTask()
    }

    //--------------------------------------------------------------------------------------------------------------------------------------//
    //Bottom sheet view model code//
    fun navigateToNote() {
        _navigateToNote.value = true
    }


    fun navigateToTime() {
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

    fun navigateBack2() {
        _navigateBackToHomePage.value = true
    }

    private fun showError() {
        Toast.makeText(application, "No Text Entered", Toast.LENGTH_SHORT).show()
    }

    fun finishedNav2() {
        _navigateBackToHomePage.value = false
    }

    private fun startTimer(calendar: Calendar?, taskIdd: Long) {
        val alarmManager =
            application.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val intent = Intent(application, AlarmReceiver::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
        intent.putExtra("taskId", taskIdd)
        intent.putExtra("message", header2.value.toString())
        val pendingIntent = PendingIntent.getBroadcast(
            application,
            (Date().time / 1000L % Int.MAX_VALUE).toInt(),
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT
        )
        alarmManager.setExact(AlarmManager.RTC_WAKEUP, calendar!!.timeInMillis, pendingIntent)
    }

// End of bottom sheet view model code


    val chipFilterResults: LiveData<List<Task>> = _forceUpdate.switchMap { hey: Boolean ->
        if (hey) {
            return@switchMap repository.getAllTasks().switchMap {
                chipFiltering(it)
            }
        }
        return@switchMap _tasks
    }


    private fun chipFiltering(list: List<Task>): LiveData<List<Task>> {
        val list4 = MutableLiveData<List<Task>>()
        val list2 = ArrayList<Task>(list)
        when (currentFiltering.value) {

            FilterType.ACTIVE_TASKS -> {
                list4.value = list
                return list4
            }
            FilterType.LATER_TASKS -> {
                list2.sortWith(compareByDescending { it.timeInMillis })
                list4.value = list2
                return list4
            }
            FilterType.TODAY_TASKS -> {
                val list3 = ArrayList<Task>()
                val calender = Calendar.getInstance()
                calender.add(Calendar.DAY_OF_MONTH, 1)
                calender.set(Calendar.HOUR_OF_DAY, 0)
                calender.set(Calendar.MINUTE, 0)
                calender.set(Calendar.SECOND, 0)
                calender.set(Calendar.MILLISECOND, 0)

                val millisUntilMidnight: Long = (calender.timeInMillis - System.currentTimeMillis())
                for (tasks in list2) {
                    if (((tasks.timeInMillis - System.currentTimeMillis()) < millisUntilMidnight) &&
                        (tasks.timeInMillis != 0L)
                    )
                        list3.add(tasks)
                }
                list3.sortWith(compareByDescending { it.timeInMillis })
                list4.value = list3
                return list4
            }

            else -> return list4
        }

    }


    val searchResultList: LiveData<List<Task>> = Transformations.switchMap(_query, ::filterIt)

    private fun filterIt(query: String?) = repository.searchTask(query)


    val filterNow = MutableLiveData<Boolean>(false)


    fun onChipClicked(filterType: String) {

        currentFiltering.value = when (filterType) {
            "TODAY_TASKS" -> FilterType.TODAY_TASKS
            "COMPLETED_TASKS" -> FilterType.COMPLETED_TASKS
            "LATER_TASKS" -> FilterType.LATER_TASKS
            else -> FilterType.ACTIVE_TASKS
        }
        _forceUpdate.value = true
    }


    init {
        _startSearch.value = false
        searchNow("")
        //        initializeLatestTask()
    }

    fun searchNow(query: String?) {
        _query.value = query
    }

    //here we are gonna change the filter type set as default using this method
    fun setFiltering(filter: FilterType) {
        currentFiltering.value = filter
//depending on the filter type,we set the filtering label, icon labels, etc.
    }

    fun startSearch() {
        _startSearch.value = true
    }

    fun onFabClicked() {
        _navigateToBottomSheet.value = true
    }

    fun onNavigatedToSearch() {
        _navigateToBottomSheet.value = false
    }


    public fun fetchTaskInfo(taskId: Long) {
        viewModelScope.launch {
            task.value = repository.getTask(taskId)
            header.value = task.value?.header
            body.value = task.value?.body
        }
    }

    public fun showReminder() {
        _navigateToDatePicker.value = true
    }


    public fun deleteTask() {
        viewModelScope.launch {

            repository.delete(task.value)
        }
        _navigateBackToHome.value = true
    }

    var header = MutableLiveData<String>()
    var body = MutableLiveData<String>()


    public fun addOrRemoveReminder(calendar: Calendar?) {

    }

    public fun updateTask() {
        viewModelScope.launch {
            task.value!!.header = header.value as String
            task.value!!.body = body.value
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


    override fun onCleared() {
        super.onCleared()
        job.cancel()
    }

    fun finishedSettingReminder() {
        _navigateToDatePicker.value = false
    }


}