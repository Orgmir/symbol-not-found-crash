package com.example.crashycrash

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform