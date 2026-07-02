package com.example.tasktracker.data.repository

import com.example.tasktracker.data.local.entity.CategoryEntity
import com.example.tasktracker.data.local.entity.TaskEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.map

class FakeTaskRepository : TaskRepository {
    private val tasksList = mutableListOf<TaskEntity>()
    private val categoriesList = mutableListOf<CategoryEntity>()

    private val _tasks = MutableStateFlow<List<TaskEntity>>(emptyList())
    private val _categories = MutableStateFlow<List<CategoryEntity>>(emptyList())

    private var taskIdCounter = 1L
    private var categoryIdCounter = 1L

    private fun updateTasksFlow() {
        _tasks.value = tasksList.toList()
    }

    private fun updateCategoriesFlow() {
        _categories.value = categoriesList.toList()
    }

    override fun getTasks(): Flow<List<TaskEntity>> = _tasks

    override fun getTasksByCategory(categoryId: Long): Flow<List<TaskEntity>> {
        return _tasks.map { list -> list.filter { it.categoryId == categoryId } }
    }

    override fun getTask(id: Long): Flow<TaskEntity?> {
        return _tasks.map { list -> list.find { it.id == id } }
    }

    override suspend fun insertTask(task: TaskEntity): Long {
        val id = if (task.id == 0L) taskIdCounter++ else task.id
        val taskWithId = task.copy(id = id)
        tasksList.removeIf { it.id == id }
        tasksList.add(taskWithId)
        updateTasksFlow()
        return id
    }

    override suspend fun updateTask(task: TaskEntity) {
        val index = tasksList.indexOfFirst { it.id == task.id }
        if (index != -1) {
            tasksList[index] = task
            updateTasksFlow()
        }
    }

    override suspend fun deleteTask(task: TaskEntity) {
        tasksList.removeIf { it.id == task.id }
        updateTasksFlow()
    }

    override suspend fun completeTask(taskId: Long, isCompleted: Boolean) {
        val index = tasksList.indexOfFirst { it.id == taskId }
        if (index != -1) {
            val task = tasksList[index]
            tasksList[index] = task.copy(
                isCompleted = isCompleted,
                completedDate = if (isCompleted) System.currentTimeMillis() else null
            )
            updateTasksFlow()
        }
    }

    override fun getCategories(): Flow<List<CategoryEntity>> = _categories

    override suspend fun insertCategory(category: CategoryEntity): Long {
        val id = if (category.id == 0L) categoryIdCounter++ else category.id
        val categoryWithId = category.copy(id = id)
        categoriesList.removeIf { it.id == id }
        categoriesList.add(categoryWithId)
        updateCategoriesFlow()
        return id
    }

    override suspend fun deleteCategory(category: CategoryEntity) {
        categoriesList.removeIf { it.id == category.id }
        updateCategoriesFlow()
    }
}
