package com.example.tasktracker.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import com.example.tasktracker.data.model.Priority

@Entity(
    tableName = "tasks",
    foreignKeys = [
        ForeignKey(
            entity = CategoryEntity::class,
            parentColumns = ["id"],
            childColumns = ["category_id"],
            onDelete = ForeignKey.SET_NULL
        )
    ],
    indices = [Index(value = ["category_id"])]
)
data class TaskEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val title: String,
    val description: String,
    @ColumnInfo(name = "created_date")
    val createdDate: Long,
    @ColumnInfo(name = "due_date")
    val dueDate: Long?,
    val priority: Priority,
    @ColumnInfo(name = "is_completed")
    val isCompleted: Boolean,
    @ColumnInfo(name = "completed_date")
    val completedDate: Long?,
    @ColumnInfo(name = "category_id")
    val categoryId: Long?
)
