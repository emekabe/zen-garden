package com.example.tasktracker.data.model

enum class Priority(val displayName: String, val colorHex: Long) {
    LOW("Low", 0xFF6B8E23),     // Olive Drab (Nature/Earthy Low)
    MEDIUM("Medium", 0xFFCD853F), // Peru/Sandy Brown (Nature/Earthy Medium)
    HIGH("High", 0xFFB22222)     // Firebrick/Clay Red (Nature/Earthy High)
}
