package com.example.todolist.di

import android.app.Application
import androidx.room.Dao
import com.example.todolist.TaskDatabase
import com.example.todolist.TaskDatabaseDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ApplicationComponent
import javax.inject.Singleton


@InstallIn(ApplicationComponent::class)
@Module
object LocalDatabaseModule {

    @Provides
    fun provideDao(taskDatabase: TaskDatabase): TaskDatabaseDao {
        return taskDatabase.taskDatabaseDao
    }

    //now remember TaskDatabase is not a class we can simply annotate with @Inject cause this class belongs to room database and is extending RoomDatabase

    //because we want a single instance of the database
    @Singleton
    @Provides
    fun provideTaskDatabase(application: Application): TaskDatabase {
        return TaskDatabase.getInstance(application)
    }


}