package com.example.learn1

import androidx.room.Entity
import androidx.room.PrimaryKey

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
    var id:Int
)