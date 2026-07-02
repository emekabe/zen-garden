package com.example.tasktracker.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Flag
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.tasktracker.data.local.entity.CategoryEntity
import com.example.tasktracker.data.local.entity.TaskEntity
import com.example.tasktracker.data.model.Priority
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun TaskItem(
    task: TaskEntity,
    category: CategoryEntity?,
    onToggleCompletion: () -> Unit,
    onDelete: () -> Unit,
    onEdit: () -> Unit,
    modifier: Modifier = Modifier
) {
    var isExpanded by remember { mutableStateOf(false) }
    
    val checkboxColor by animateColorAsState(
        targetValue = if (task.isCompleted) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.4f),
        label = "checkboxColor"
    )

    val cardBgColor by animateColorAsState(
        targetValue = if (task.isCompleted) {
            MaterialTheme.colorScheme.surface.copy(alpha = 0.6f)
        } else {
            MaterialTheme.colorScheme.surface
        },
        label = "cardBgColor"
    )

    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 6.dp)
            .clip(RoundedCornerShape(16.dp))
            .clickable { isExpanded = !isExpanded },
        colors = CardDefaults.cardColors(containerColor = cardBgColor),
        elevation = CardDefaults.cardElevation(defaultElevation = if (task.isCompleted) 0.dp else 1.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                // Checkbox
                Checkbox(
                    checked = task.isCompleted,
                    onCheckedChange = { onToggleCompletion() },
                    colors = CheckboxDefaults.colors(
                        checkedColor = MaterialTheme.colorScheme.primary,
                        uncheckedColor = checkboxColor,
                        checkmarkColor = MaterialTheme.colorScheme.background
                    )
                )
                
                Spacer(modifier = Modifier.width(8.dp))

                // Title and basic stats
                Column(
                    modifier = Modifier.weight(1f)
                ) {
                    Text(
                        text = task.title,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = if (task.isCompleted) {
                            MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
                        } else {
                            MaterialTheme.colorScheme.onSurface
                        },
                        textDecoration = if (task.isCompleted) TextDecoration.LineThrough else TextDecoration.None,
                        maxLines = if (isExpanded) Int.MAX_VALUE else 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    
                    if (!isExpanded && task.description.isNotEmpty()) {
                        Text(
                            text = task.description,
                            fontSize = 13.sp,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }

                // Priority Indicator dot / small flag
                PriorityBadge(priority = task.priority)
            }

            // Expanded view details
            AnimatedVisibility(visible = isExpanded) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = 48.dp, top = 8.dp)
                ) {
                    if (task.description.isNotEmpty()) {
                        Text(
                            text = task.description,
                            fontSize = 14.sp,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f),
                            lineHeight = 20.sp
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                    }

                    // Metadata row: Category, Due Date, Dates
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Category Tag
                        category?.let { cat ->
                            CategoryTag(category = cat)
                            Spacer(modifier = Modifier.width(8.dp))
                        }

                        // Due Date
                        task.dueDate?.let { due ->
                            DueDateTag(dueDateTimestamp = due)
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // Date created & completed details
                    Text(
                        text = "Planted: ${formatDate(task.createdDate)}",
                        fontSize = 11.sp,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.4f)
                    )
                    if (task.isCompleted && task.completedDate != null) {
                        Text(
                            text = "Watered: ${formatDate(task.completedDate)}",
                            fontSize = 11.sp,
                            color = MaterialTheme.colorScheme.primary.copy(alpha = 0.7f)
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    // Actions
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Spacer(modifier = Modifier.weight(1f))
                        
                        IconButton(onClick = onEdit) {
                            Icon(
                                imageVector = Icons.Default.Edit,
                                contentDescription = "Edit task",
                                tint = MaterialTheme.colorScheme.primary.copy(alpha = 0.7f),
                                modifier = Modifier.size(20.dp)
                            )
                        }

                        IconButton(onClick = onDelete) {
                            Icon(
                                imageVector = Icons.Default.Delete,
                                contentDescription = "Delete task",
                                tint = Color(0xFFB24D3D).copy(alpha = 0.8f),
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun PriorityBadge(priority: Priority) {
    val color = when (priority) {
        Priority.LOW -> Color(0xFF7A8F60)      // Moss
        Priority.MEDIUM -> Color(0xFFD4A356)   // Ochre
        Priority.HIGH -> Color(0xFFB24D3D)     // Clay
    }
    
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .background(
                color = color.copy(alpha = 0.15f),
                shape = RoundedCornerShape(8.dp)
            )
            .padding(horizontal = 8.dp, vertical = 4.dp)
    ) {
        Icon(
            imageVector = Icons.Default.Flag,
            contentDescription = "Priority",
            tint = color,
            modifier = Modifier.size(12.dp)
        )
        Spacer(modifier = Modifier.width(4.dp))
        Text(
            text = priority.displayName,
            fontSize = 10.sp,
            fontWeight = FontWeight.Bold,
            color = color
        )
    }
}

@Composable
fun CategoryTag(category: CategoryEntity) {
    val catColor = Color(category.colorHex)
    
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .background(
                color = catColor.copy(alpha = 0.15f),
                shape = RoundedCornerShape(8.dp)
            )
            .padding(horizontal = 8.dp, vertical = 4.dp)
    ) {
        Box(
            modifier = Modifier
                .size(8.dp)
                .clip(CircleShape)
                .background(catColor)
        )
        Spacer(modifier = Modifier.width(6.dp))
        Text(
            text = category.name,
            fontSize = 11.sp,
            fontWeight = FontWeight.Medium,
            color = if (MaterialTheme.colorScheme.background == Color(0xFFFAF7F2)) {
                // Light theme: draw dark color for better contrast
                catColor.copy(alpha = 0.9f)
            } else {
                MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f)
            }
        )
    }
}

@Composable
fun DueDateTag(dueDateTimestamp: Long) {
    val sdf = SimpleDateFormat("MMM d, yyyy", Locale.getDefault())
    val dateText = sdf.format(Date(dueDateTimestamp))
    val isOverdue = dueDateTimestamp < System.currentTimeMillis() && Date().day != Date(dueDateTimestamp).day
    
    val color = if (isOverdue) Color(0xFFB24D3D) else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
    val bgColor = if (isOverdue) Color(0xFFB24D3D).copy(alpha = 0.12f) else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.06f)

    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .background(
                color = bgColor,
                shape = RoundedCornerShape(8.dp)
            )
            .padding(horizontal = 8.dp, vertical = 4.dp)
    ) {
        Icon(
            imageVector = Icons.Default.CalendarMonth,
            contentDescription = "Due Date",
            tint = color,
            modifier = Modifier.size(12.dp)
        )
        Spacer(modifier = Modifier.width(4.dp))
        Text(
            text = dateText,
            fontSize = 11.sp,
            fontWeight = FontWeight.Medium,
            color = color
        )
    }
}

private fun formatDate(timestamp: Long): String {
    val sdf = SimpleDateFormat("MMM d, yyyy, h:mm a", Locale.getDefault())
    return sdf.format(Date(timestamp))
}
