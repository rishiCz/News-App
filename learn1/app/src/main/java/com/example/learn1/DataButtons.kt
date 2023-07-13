package com.example.learn1

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class DataButtons(
    val name: String,
    val query: String,
    @PrimaryKey
    val id: Int
)
