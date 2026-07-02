package com.example.tasktracker

import android.app.Application
import com.example.tasktracker.di.AppContainer
import com.example.tasktracker.di.AppDataContainer

class TaskTrackerApplication : Application() {
    lateinit var container: AppContainer

    override fun onCreate() {
        super.onCreate()
        container = AppDataContainer(this)
    }
}
