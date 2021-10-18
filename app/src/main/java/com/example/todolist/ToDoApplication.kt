package com.example.todolist

import android.app.Application
import androidx.appcompat.app.AppCompatDelegate
import androidx.preference.PreferenceManager
import com.example.todolist.util.PREF_DARK_THEME
import dagger.hilt.android.HiltAndroidApp
import timber.log.Timber

//this annotation triggers hilt's code generation including a base class for you application that can use dependency injection. The application container is
// the parent container of this app, which means that other containers can access the dependencies that it provides.
@HiltAndroidApp
class ToDoApplication : Application() {
    override fun onCreate() {
        super.onCreate()

        val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(applicationContext)
        val mode = sharedPreferences.getBoolean(PREF_DARK_THEME,false)
        if (mode) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_AUTO_BATTERY)
        }

    }
}