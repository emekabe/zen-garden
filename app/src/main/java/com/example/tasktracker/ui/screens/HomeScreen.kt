package com.example.tasktracker.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material.icons.filled.Grass
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Spa
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.tasktracker.data.local.entity.TaskEntity
import com.example.tasktracker.ui.components.TaskEntryDialog
import com.example.tasktracker.ui.components.TaskFilterBar
import com.example.tasktracker.ui.components.TaskItem
import com.example.tasktracker.ui.components.ZenGrowthWidget
import com.example.tasktracker.ui.viewmodel.TaskViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun HomeScreen(
    viewModel: TaskViewModel,
    modifier: Modifier = Modifier
) {
    val uiState by viewModel.uiState.collectAsState()

    var showEntryDialog by remember { mutableStateOf(false) }
    var taskToEdit by remember { mutableStateOf<TaskEntity?>(null) }
    
    var completedTasksExpanded by remember { mutableStateOf(false) }

    val activeTasks = uiState.tasks.filter { !it.isCompleted }
    val completedTasks = uiState.tasks.filter { it.isCompleted }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    taskToEdit = null
                    showEntryDialog = true
                },
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.background,
                shape = CircleShape
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Sow new task",
                    modifier = Modifier.size(28.dp)
                )
            }
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            // Header Section
            HeaderView(
                searchQuery = uiState.searchQuery,
                onSearchChange = { viewModel.updateSearchQuery(it) }
            )

            // Scrollable Layout
            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
            ) {
                // Zen Garden Plant Growth
                item {
                    ZenGrowthWidget(
                        completionRate = uiState.completionRate,
                        activeTasksCount = uiState.activeTasksCount,
                        completedTasksCount = uiState.completedTasksCount
                    )
                }

                // Filters
                item {
                    TaskFilterBar(
                        categories = uiState.categories,
                        selectedCategoryId = uiState.selectedCategoryId,
                        onSelectCategory = { viewModel.selectCategory(it) },
                        selectedPriority = uiState.selectedPriority,
                        onSelectPriority = { viewModel.selectPriority(it) }
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                }

                // Section Title: Active Tasks
                item {
                    Text(
                        text = "Active Tasks (${activeTasks.size})",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 6.dp)
                    )
                }

                // Empty State
                if (activeTasks.isEmpty()) {
                    item {
                        EmptyActiveState(isFiltering = uiState.selectedCategoryId != null || uiState.selectedPriority != null || uiState.searchQuery.isNotEmpty())
                    }
                } else {
                    items(activeTasks, key = { it.id }) { task ->
                        val cat = uiState.categories.find { it.id == task.categoryId }
                        TaskItem(
                            task = task,
                            category = cat,
                            onToggleCompletion = { viewModel.toggleTaskCompletion(task) },
                            onDelete = { viewModel.deleteTask(task) },
                            onEdit = {
                                taskToEdit = task
                                showEntryDialog = true
                            }
                        )
                    }
                }

                // Section: Collapsible Completed/Harvested Tasks
                if (completedTasks.isNotEmpty()) {
                    item {
                        Spacer(modifier = Modifier.height(16.dp))
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { completedTasksExpanded = !completedTasksExpanded }
                                .padding(horizontal = 16.dp, vertical = 12.dp)
                        ) {
                            Icon(
                                imageVector = if (completedTasksExpanded) Icons.Default.ExpandMore else Icons.Default.ChevronRight,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f),
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "Harvested Tasks (${completedTasks.size})",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                            )
                        }
                    }

                    if (completedTasksExpanded) {
                        items(completedTasks, key = { it.id }) { task ->
                            val cat = uiState.categories.find { it.id == task.categoryId }
                            TaskItem(
                                task = task,
                                category = cat,
                                onToggleCompletion = { viewModel.toggleTaskCompletion(task) },
                                onDelete = { viewModel.deleteTask(task) },
                                onEdit = {
                                    taskToEdit = task
                                    showEntryDialog = true
                                },
                                modifier = Modifier.animateItem()
                            )
                        }
                    }
                }

                // Add bottom space
                item {
                    Spacer(modifier = Modifier.height(80.dp))
                }
            }
        }
    }

    // Modal Task Entry
    if (showEntryDialog) {
        TaskEntryDialog(
            categories = uiState.categories,
            taskToEdit = taskToEdit,
            onDismiss = { showEntryDialog = false },
            onConfirm = { title, description, categoryId, priority, dueDate ->
                if (taskToEdit == null) {
                    viewModel.insertTask(title, description, categoryId, priority, dueDate)
                } else {
                    viewModel.updateTask(
                        taskToEdit!!.copy(
                            title = title,
                            description = description,
                            categoryId = categoryId,
                            priority = priority,
                            dueDate = dueDate
                        )
                    )
                }
                showEntryDialog = false
            }
        )
    }
}

@Composable
fun HeaderView(
    searchQuery: String,
    onSearchChange: (String) -> Unit
) {
    val sdf = SimpleDateFormat("EEEE, MMMM d", Locale.getDefault())
    val currentDate = sdf.format(Date())

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 16.dp, start = 16.dp, end = 16.dp, bottom = 8.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Default.Spa,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(28.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = "Zen Garden",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )

        }
        Text(
            text = currentDate,
            fontSize = 13.sp,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
        )
        
        Spacer(modifier = Modifier.height(14.dp))

        // Search Bar
        OutlinedTextField(
            value = searchQuery,
            onValueChange = onSearchChange,
            placeholder = { Text("Search your tasks...") },
            leadingIcon = {
                Icon(
                    imageVector = Icons.Default.Search,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.4f)
                )
            },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(14.dp),
            singleLine = true,
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = MaterialTheme.colorScheme.primary,
                unfocusedBorderColor = MaterialTheme.colorScheme.surfaceVariant,
                focusedContainerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.2f),
                unfocusedContainerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.2f)
            )

        )
    }
}

@Composable
fun EmptyActiveState(
    isFiltering: Boolean,
    modifier: Modifier = Modifier
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier
            .fillMaxWidth()
            .padding(top = 32.dp, bottom = 48.dp, start = 32.dp, end = 32.dp)
    ) {
        Icon(
            imageVector = Icons.Default.Grass,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.primary.copy(alpha = 0.25f),
            modifier = Modifier.size(64.dp)
        )
        Spacer(modifier = Modifier.height(12.dp))
        Text(
            text = if (isFiltering) "No matching tasks found." else "Your garden is quiet.",
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = if (isFiltering) "Try refining your search filter." else "Sow a new task and watch it grow into bloom!",
            fontSize = 13.sp,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.4f),
            textAlign = TextAlign.Center
        )
    }
}
