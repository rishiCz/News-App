package com.example.learn1.DataClass

import androidx.room.Entity
import androidx.room.OnConflictStrategy
import androidx.room.PrimaryKey
import java.io.Serializable

@Entity
data class DataNews(
    var title : String,
    var author :String,
    var url :String,
    var urlToImage : String,
    var publishedAt : String,
    var description : String,
    var content : String,
    var saved : Boolean,
    @PrimaryKey(autoGenerate = true)
    var id:Long
): Serializable