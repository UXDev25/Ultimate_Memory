package org.example.project.memory

import android.annotation.SuppressLint
import android.content.Context

@SuppressLint("StaticFieldLeak")
object AppContextHolder {
    private var _context: Context? = null
    val context: Context
        get() = _context
            ?: throw IllegalStateException("AppContextHolder has not been initialized")
    fun init(context: Context) {
        _context = context.applicationContext
    }
}