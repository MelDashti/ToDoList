package com.example.todolist

import android.app.Application
import timber.log.Timber

public class ToDoApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        val timber = Timber.plant(Timber.DebugTree())
    }
}