package org.example.project.memory.screens

class Proxy {
    fun String.toProxyUrl(): String {
        val proxyPrefix = "https://api.codetabs.com/v1/proxy?quest="
        return "$proxyPrefix${this}"
    }
}