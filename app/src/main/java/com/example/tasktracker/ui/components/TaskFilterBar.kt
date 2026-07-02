package com.example.tasktracker.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AllInclusive
import androidx.compose.material.icons.filled.DirectionsRun
import androidx.compose.material.icons.filled.Lightbulb
import androidx.compose.material.icons.filled.LocalFlorist
import androidx.compose.material.icons.filled.SelfImprovement
import androidx.compose.material.icons.filled.WaterDrop
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text

import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.tasktracker.data.local.entity.CategoryEntity
import com.example.tasktracker.data.model.Priority

@Composable
fun TaskFilterBar(
    categories: List<CategoryEntity>,
    selectedCategoryId: Long?,
    onSelectCategory: (Long?) -> Unit,
    selectedPriority: Priority?,
    onSelectPriority: (Priority?) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
    ) {
        // Section header
        Text(
            text = "Categories",
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f),
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp)
        )

        // Categories list
        LazyRow(
            modifier = Modifier.fillMaxWidth(),
            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 4.dp)
        ) {
            // "All" Category Chip
            item {
                FilterChip(
                    label = "All Garden",
                    isSelected = selectedCategoryId == null,
                    onClick = { onSelectCategory(null) },
                    icon = Icons.Default.AllInclusive,
                    badgeColor = MaterialTheme.colorScheme.primary
                )
                Spacer(modifier = Modifier.width(8.dp))
            }

            items(categories) { category ->
                FilterChip(
                    label = category.name,
                    isSelected = selectedCategoryId == category.id,
                    onClick = { onSelectCategory(category.id) },
                    icon = getIconForName(category.iconName),
                    badgeColor = Color(category.colorHex)
                )
                Spacer(modifier = Modifier.width(8.dp))
            }
        }

        Spacer(modifier = Modifier.height(6.dp))

        // Priorities List
        Text(
            text = "Priorities",
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f),
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp)
        )

        LazyRow(
            modifier = Modifier.fillMaxWidth(),
            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 4.dp)
        ) {
            item {
                FilterChip(
                    label = "All Priorities",
                    isSelected = selectedPriority == null,
                    onClick = { onSelectPriority(null) },
                    icon = null,
                    badgeColor = Color.Gray
                )
                Spacer(modifier = Modifier.width(8.dp))
            }

            items(Priority.values()) { priority ->
                val priorityColor = when (priority) {
                    Priority.LOW -> Color(0xFF7A8F60)
                    Priority.MEDIUM -> Color(0xFFD4A356)
                    Priority.HIGH -> Color(0xFFB24D3D)
                }
                FilterChip(
                    label = priority.displayName,
                    isSelected = selectedPriority == priority,
                    onClick = { onSelectPriority(priority) },
                    icon = null,
                    badgeColor = priorityColor
                )
                Spacer(modifier = Modifier.width(8.dp))
            }
        }
    }
}

@Composable
fun FilterChip(
    label: String,
    isSelected: Boolean,
    onClick: () -> Unit,
    icon: ImageVector?,
    badgeColor: Color,
    modifier: Modifier = Modifier
) {
    val bgColor = if (isSelected) {
        MaterialTheme.colorScheme.primary.copy(alpha = 0.15f)
    } else {
        MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f)
    }

    val borderColor = if (isSelected) {
        MaterialTheme.colorScheme.primary
    } else {
        Color.Transparent
    }

    val textColor = if (isSelected) {
        MaterialTheme.colorScheme.primary
    } else {
        MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f)
    }

    Row(
        modifier = modifier
            .clip(RoundedCornerShape(12.dp))
            .background(bgColor)
            .clickable { onClick() }
            .padding(horizontal = 12.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        if (icon != null) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = if (isSelected) MaterialTheme.colorScheme.primary else badgeColor,
                modifier = Modifier.size(16.dp)
            )
            Spacer(modifier = Modifier.width(6.dp))
        } else {
            // Draw a color dot instead
            Box(
                modifier = Modifier
                    .size(8.dp)
                    .clip(CircleShape)
                    .background(badgeColor)
            )
            Spacer(modifier = Modifier.width(6.dp))
        }

        Text(
            text = label,
            fontSize = 13.sp,
            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
            color = textColor
        )
    }
}

fun getIconForName(iconName: String): ImageVector {
    return when (iconName) {
        "LocalFlorist" -> Icons.Default.LocalFlorist
        "SelfImprovement" -> Icons.Default.SelfImprovement
        "Lightbulb" -> Icons.Default.Lightbulb
        "WaterDrop" -> Icons.Default.WaterDrop
        "DirectionsRun" -> Icons.Default.DirectionsRun
        else -> Icons.Default.LocalFlorist
    }
}
