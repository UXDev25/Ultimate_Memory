package org.example.project.memory

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform