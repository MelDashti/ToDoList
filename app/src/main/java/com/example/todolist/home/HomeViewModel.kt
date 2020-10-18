package com.example.todolist.home

import android.app.Application
import android.content.Context
import android.widget.Toast
import androidx.lifecycle.*
import com.example.todolist.Task
import com.example.todolist.TaskDatabaseDao
import com.example.todolist.TaskDatabaseDao_Impl
import com.example.todolist.repository.TaskRepository
import kotlinx.coroutines.*
import java.util.*
import kotlin.collections.ArrayList

public class HomeViewModel(private val repository: TaskRepository) :
    ViewModel() {

    private var currentFiltering = MutableLiveData<FilterType>(FilterType.ACTIVE_TASKS)

    //this will update your list
    private val _forceUpdate = MutableLiveData<Boolean>(false)


    private val _tasks = repository.getAllTasks()

    val tasks: LiveData<List<Task>> = _tasks

    private val viewModelJob = Job()

    private val uiScope = CoroutineScope(Dispatchers.Main + viewModelJob)

    private val _navigateToBottomSheet = MutableLiveData<Boolean>()

    val navigateToBottomSheet: LiveData<Boolean> = _navigateToBottomSheet

    private val _query = MutableLiveData<String>()

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
        Toast.makeText(repository.fapplication, "HEYYYY", Toast.LENGTH_LONG).show()

        currentFiltering.value = when (filterType) {
            "TODAY_TASKS" -> FilterType.TODAY_TASKS
            "COMPLETED_TASKS" -> FilterType.COMPLETED_TASKS
            "LATER_TASKS" -> FilterType.LATER_TASKS
            else -> FilterType.ACTIVE_TASKS
        }
        _forceUpdate.value = true
    }


    init {
//        initializeLatestTask()
    }

    fun searchNow(query: String?) {
        _query.value = query
    }


    override fun onCleared() {
        super.onCleared()
        viewModelJob.cancel()
    }

    //here we are gonna change the filter type set as default usinig this method
    fun setFiltering(filter: FilterType) {
        currentFiltering.value = filter
//depending on the filter type,we set the filtering label, icon labels, etc.

    }

    fun onFabClicked() {
        _navigateToBottomSheet.value = true
    }

    fun onNavigatedToSearch() {
        _navigateToBottomSheet.value = false
    }
}

//    private val _latestTask = MutableLiveData<Task?>()

//    private fun initializeLatestTask() {
//        uiScope.launch {
//            _latestTask.value = repository.getLatestTaskFromDatabase()
//        }
//    }


@Suppress("UNCHECKED_CAST")
class TasksViewModelFactory(
    private val tasksRepository: TaskRepository
) : ViewModelProvider.NewInstanceFactory() {
    override fun <T : ViewModel> create(modelClass: Class<T>) =
        (HomeViewModel(tasksRepository) as T)
}