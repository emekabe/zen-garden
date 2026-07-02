package com.example.tasktracker.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverter
import androidx.room.TypeConverters
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.tasktracker.data.local.dao.CategoryDao
import com.example.tasktracker.data.local.dao.TaskDao
import com.example.tasktracker.data.local.entity.CategoryEntity
import com.example.tasktracker.data.local.entity.TaskEntity
import com.example.tasktracker.data.model.Priority
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class PriorityConverter {
    @TypeConverter
    fun fromPriority(priority: Priority): String = priority.name

    @TypeConverter
    fun toPriority(value: String): Priority {
        return try {
            Priority.valueOf(value)
        } catch (e: Exception) {
            Priority.LOW
        }
    }
}

@Database(entities = [TaskEntity::class, CategoryEntity::class], version = 1, exportSchema = false)
@TypeConverters(PriorityConverter::class)
abstract class TaskDatabase : RoomDatabase() {

    abstract fun taskDao(): TaskDao
    abstract fun categoryDao(): CategoryDao

    companion object {
        @Volatile
        private var INSTANCE: TaskDatabase? = null

        fun getDatabase(context: Context, scope: CoroutineScope): TaskDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    TaskDatabase::class.java,
                    "task_tracker_database"
                )
                    .addCallback(DatabaseCallback(scope))
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }

    private class DatabaseCallback(
        private val scope: CoroutineScope
    ) : RoomDatabase.Callback() {
        override fun onCreate(db: SupportSQLiteDatabase) {
            super.onCreate(db)
            INSTANCE?.let { database ->
                scope.launch(Dispatchers.IO) {
                    val categoryDao = database.categoryDao()
                    
                    // Prepopulate nature categories
                    categoryDao.insertCategory(
                        CategoryEntity(
                            name = "Garden & Growth",
                            colorHex = 0xFF2E6F40.toInt(),
                            iconName = "LocalFlorist"
                        )
                    )
                    categoryDao.insertCategory(
                        CategoryEntity(
                            name = "Rest & Mindfulness",
                            colorHex = 0xFF8A9A5B.toInt(), // Moss green
                            iconName = "SelfImprovement"
                        )
                    )
                    categoryDao.insertCategory(
                        CategoryEntity(
                            name = "Work & Ideas",
                            colorHex = 0xFFC2B280.toInt(), // Sand
                            iconName = "Lightbulb"
                        )
                    )
                    categoryDao.insertCategory(
                        CategoryEntity(
                            name = "Wellness & Flow",
                            colorHex = 0xFF5F9EA0.toInt(), // Cadet blue / Water
                            iconName = "WaterDrop"
                        )
                    )
                    categoryDao.insertCategory(
                        CategoryEntity(
                            name = "Vitality & Strength",
                            colorHex = 0xFFB2533E.toInt(), // Earth red
                            iconName = "DirectionsRun"
                        )
                    )
                }
            }
        }
    }
}
