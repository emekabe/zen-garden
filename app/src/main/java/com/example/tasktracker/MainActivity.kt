package com.example.tasktracker

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.example.tasktracker.ui.screens.HomeScreen
import com.example.tasktracker.ui.screens.SplashScreen
import com.example.tasktracker.ui.theme.TaskTrackerTheme
import com.example.tasktracker.ui.viewmodel.TaskViewModel

class MainActivity : ComponentActivity() {
    
    private val viewModel: TaskViewModel by viewModels { TaskViewModel.Factory }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            TaskTrackerTheme {
                var showSplash by remember { mutableStateOf(true) }
                if (showSplash) {
                    SplashScreen(onAnimationFinished = { showSplash = false })
                } else {
                    HomeScreen(viewModel = viewModel)
                }
            }
        }
    }
}