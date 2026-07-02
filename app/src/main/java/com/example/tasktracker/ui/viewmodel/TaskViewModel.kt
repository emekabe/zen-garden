package com.example.tasktracker.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.tasktracker.TaskTrackerApplication
import com.example.tasktracker.data.local.entity.CategoryEntity
import com.example.tasktracker.data.local.entity.TaskEntity
import com.example.tasktracker.data.model.Priority
import com.example.tasktracker.data.repository.TaskRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

data class HomeUiState(
    val tasks: List<TaskEntity> = emptyList(),
    val categories: List<CategoryEntity> = emptyList(),
    val selectedCategoryId: Long? = null,
    val selectedPriority: Priority? = null,
    val searchQuery: String = "",
    val completionRate: Float = 0f,
    val activeTasksCount: Int = 0,
    val completedTasksCount: Int = 0
)

class TaskViewModel(private val repository: TaskRepository) : ViewModel() {

    private val _selectedCategoryId = MutableStateFlow<Long?>(null)
    private val _selectedPriority = MutableStateFlow<Priority?>(null)
    private val _searchQuery = MutableStateFlow("")

    val uiState: StateFlow<HomeUiState> = combine(
        repository.getTasks(),
        repository.getCategories(),
        _selectedCategoryId,
        _selectedPriority,
        _searchQuery
    ) { tasks, categories, selectedCategory, selectedPriority, query ->
        
        // Filter tasks
        val filteredTasks = tasks.filter { task ->
            val matchesCategory = selectedCategory == null || task.categoryId == selectedCategory
            val matchesPriority = selectedPriority == null || task.priority == selectedPriority
            val matchesSearch = query.isEmpty() || 
                    task.title.contains(query, ignoreCase = true) || 
                    task.description.contains(query, ignoreCase = true)
            
            matchesCategory && matchesPriority && matchesSearch
        }

        val activeCount = tasks.count { !it.isCompleted }
        val completedCount = tasks.count { it.isCompleted }
        val totalCount = tasks.size
        
        val rate = if (totalCount > 0) {
            completedCount.toFloat() / totalCount.toFloat()
        } else {
            0f
        }

        HomeUiState(
            tasks = filteredTasks,
            categories = categories,
            selectedCategoryId = selectedCategory,
            selectedPriority = selectedPriority,
            searchQuery = query,
            completionRate = rate,
            activeTasksCount = activeCount,
            completedTasksCount = completedCount
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = HomeUiState()
    )

    fun selectCategory(categoryId: Long?) {
        _selectedCategoryId.value = categoryId
    }

    fun selectPriority(priority: Priority?) {
        _selectedPriority.value = priority
    }

    fun updateSearchQuery(query: String) {
        _searchQuery.value = query
    }

    fun insertTask(title: String, description: String, categoryId: Long?, priority: Priority, dueDate: Long?) {
        viewModelScope.launch {
            val task = TaskEntity(
                title = title,
                description = description,
                createdDate = System.currentTimeMillis(),
                dueDate = dueDate,
                priority = priority,
                isCompleted = false,
                completedDate = null,
                categoryId = categoryId
            )
            repository.insertTask(task)
        }
    }

    fun updateTask(task: TaskEntity) {
        viewModelScope.launch {
            repository.updateTask(task)
        }
    }

    fun toggleTaskCompletion(task: TaskEntity) {
        viewModelScope.launch {
            repository.completeTask(task.id, !task.isCompleted)
        }
    }

    fun deleteTask(task: TaskEntity) {
        viewModelScope.launch {
            repository.deleteTask(task)
        }
    }

    fun createCategory(name: String, colorHex: Int, iconName: String) {
        viewModelScope.launch {
            val category = CategoryEntity(
                name = name,
                colorHex = colorHex,
                iconName = iconName
            )
            repository.insertCategory(category)
        }
    }

    companion object {
        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val application = (this[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY] as TaskTrackerApplication)
                TaskViewModel(application.container.taskRepository)
            }
        }
    }
}
