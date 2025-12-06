package com.example.lingonary_.models

data class Podcast(
    val title: String,
    val description: String = "This is a podcast app built by Team Spaghetti during the fall semester of 2025.",
    val jsonFileName: String,
    val audioResId: Int
)