package com.example.todolist.di

import android.app.Application
import android.content.Context
import androidx.room.Dao
import com.example.todolist.TaskDatabase
import com.example.todolist.TaskDatabaseDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton


@InstallIn(SingletonComponent::class)
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

    @Singleton
    @Provides
    fun provideApplication(@ApplicationContext app: Context): Application {
        return app as Application
    }


}