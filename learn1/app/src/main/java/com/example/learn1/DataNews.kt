package com.example.learn1

data class DataNews(
    val title : String,
    val author : String,
    val url :String,
    val urlToImage : String,
    val publishedAt : String,
    var saved : Boolean
)