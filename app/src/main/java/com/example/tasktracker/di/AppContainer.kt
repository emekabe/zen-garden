package com.example.tasktracker.di

import android.content.Context
import com.example.tasktracker.data.local.TaskDatabase
import com.example.tasktracker.data.repository.TaskRepository
import com.example.tasktracker.data.repository.TaskRepositoryImpl
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob

interface AppContainer {
    val taskRepository: TaskRepository
}

class AppDataContainer(private val context: Context) : AppContainer {
    private val applicationScope = CoroutineScope(SupervisorJob())

    private val database: TaskDatabase by lazy {
        TaskDatabase.getDatabase(context, applicationScope)
    }

    override val taskRepository: TaskRepository by lazy {
        TaskRepositoryImpl(
            database.taskDao(),
            database.categoryDao()
        )
    }
}
