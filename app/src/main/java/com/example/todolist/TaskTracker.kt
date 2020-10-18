package com.example.todolist

import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.OnLifecycleEvent

public class TaskTracker(lifecyle : Lifecycle) : LifecycleObserver{
    init {
        lifecyle.addObserver(this)
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_START)
    fun onStart(){

    }
    @OnLifecycleEvent(Lifecycle.Event.ON_STOP)
    fun onStop(){

    }


}

