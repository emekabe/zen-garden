package com.example.tasktracker.data.repository

import com.example.tasktracker.data.local.entity.CategoryEntity
import com.example.tasktracker.data.local.entity.TaskEntity
import kotlinx.coroutines.flow.Flow

interface TaskRepository {
    fun getTasks(): Flow<List<TaskEntity>>
    fun getTasksByCategory(categoryId: Long): Flow<List<TaskEntity>>
    fun getTask(id: Long): Flow<TaskEntity?>
    suspend fun insertTask(task: TaskEntity): Long
    suspend fun updateTask(task: TaskEntity)
    suspend fun deleteTask(task: TaskEntity)
    suspend fun completeTask(taskId: Long, isCompleted: Boolean)
    
    fun getCategories(): Flow<List<CategoryEntity>>
    suspend fun insertCategory(category: CategoryEntity): Long
    suspend fun deleteCategory(category: CategoryEntity)
}
