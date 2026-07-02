package com.example.tasktracker.data.repository

import com.example.tasktracker.data.local.dao.CategoryDao
import com.example.tasktracker.data.local.dao.TaskDao
import com.example.tasktracker.data.local.entity.CategoryEntity
import com.example.tasktracker.data.local.entity.TaskEntity
import kotlinx.coroutines.flow.Flow

class TaskRepositoryImpl(
    private val taskDao: TaskDao,
    private val categoryDao: CategoryDao
) : TaskRepository {

    override fun getTasks(): Flow<List<TaskEntity>> = taskDao.getAllTasks()

    override fun getTasksByCategory(categoryId: Long): Flow<List<TaskEntity>> =
        taskDao.getTasksByCategory(categoryId)

    override fun getTask(id: Long): Flow<TaskEntity?> = taskDao.getTaskById(id)

    override suspend fun insertTask(task: TaskEntity): Long = taskDao.insertTask(task)

    override suspend fun updateTask(task: TaskEntity) = taskDao.updateTask(task)

    override suspend fun deleteTask(task: TaskEntity) = taskDao.deleteTask(task)

    override suspend fun completeTask(taskId: Long, isCompleted: Boolean) {
        val completedDate = if (isCompleted) System.currentTimeMillis() else null
        taskDao.updateTaskCompletion(taskId, isCompleted, completedDate)
    }

    override fun getCategories(): Flow<List<CategoryEntity>> = categoryDao.getAllCategories()

    override suspend fun insertCategory(category: CategoryEntity): Long =
        categoryDao.insertCategory(category)

    override suspend fun deleteCategory(category: CategoryEntity) =
        categoryDao.deleteCategory(category)
}
