package com.example.todolist

import android.app.Application
import dagger.hilt.android.HiltAndroidApp
import timber.log.Timber

//this annotation triggers hilt's code generation including a base class for you application that can use dependency injection. The application container is
// the parent container of this app, which means that other containers can access the dependencies that it provides.
@HiltAndroidApp
class ToDoApplication : Application() {
    override fun onCreate() {
        super.onCreate()
    }
}