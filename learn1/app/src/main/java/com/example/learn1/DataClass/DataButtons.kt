package com.example.learn1.DataClass

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class DataButtons(
    val name: String,
    val query: String,
    @PrimaryKey(autoGenerate = true)
    val id: Int
)
