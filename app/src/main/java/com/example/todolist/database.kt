package com.example.todolist

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.room.*
import androidx.room.Room.databaseBuilder
import java.util.*

@Database(entities = [Task::class], version = 8, exportSchema = true)
abstract class TaskDatabase : RoomDatabase() {
    abstract val taskDatabaseDao: TaskDatabaseDao

    companion object {
        @Volatile
        private var INSTANCE: TaskDatabase? = null

        fun getInstance(context: Context): TaskDatabase {
            synchronized(this) {
                var instance = INSTANCE
                if (instance == null) {
                    instance = databaseBuilder(
                        context.applicationContext,
                        TaskDatabase::class.java,
                        "task_database"
                    )
                        .fallbackToDestructiveMigration()
                        .build()
                    INSTANCE = instance
                }
                return instance
            }
        }
    }
}

@Entity(tableName = "task_table")
public data class Task(
    @PrimaryKey(autoGenerate = true)
    var _id: Long = 0L,
    var header: String = "",
    var body: String? = null,
//    var calendar: Calendar? = null,
    var timeInMillis: Long = 0L,
    var checked: Boolean = false
)

@Dao
public interface TaskDatabaseDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(task: Task): Long

    @Update
    fun update(task: Task)

    @Query(value = "select * from task_table")
    fun getAllTasks(): LiveData<List<Task>>

    @Query("select*from task_table where header like '%' || :value || '%' ")
    fun getSearchResult(value: String?): LiveData<List<Task>>

    @Query("select*from task_table order by _id desc limit 1")
    fun getLatestTask(): Task

    @Query("select*from task_table where _id = :key")
    fun get(key: Long): Task

    @Delete
    fun delete(task: Task)

    @Delete
    fun delete(task: List<Task>)

    @Query("delete from task_table ")
    fun clear()

}