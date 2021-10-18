package com.example.todolist

import android.app.AlarmManager
import android.app.Application
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.*
import com.example.todolist.home.FilterType
import com.example.todolist.receiver.AlarmReceiver
import com.example.todolist.repository.TaskRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import java.util.*
import javax.inject.Inject

@HiltViewModel
public class SharedViewModel @Inject constructor(
    private val repository: TaskRepository,
    private val application: Application
) : ViewModel() {

    private val _navigateBackToHome = MutableLiveData<Boolean>()
    public val navigateBackToHome: LiveData<Boolean>
        get() = _navigateBackToHome

    private val _navigateToDatePicker = MutableLiveData<Boolean>()
    public val navigateToDatePicker: LiveData<Boolean>
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
        _startSearch.value = false
        searchNow("")
        //        initializeLatestTask()
    }


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

    init {
    }

    override fun onCleared() {
        super.onCleared()
    }

    fun finishedSettingReminder() {
        _navigateToDatePicker.value = false
    }
}