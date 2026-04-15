package org.example.project.memory

import android.app.Application
import org.example.project.memory.AppContextHolder

class MyApp : Application() {
    override fun onCreate() {
        super.onCreate()
        AppContextHolder.init(this)
    }
}