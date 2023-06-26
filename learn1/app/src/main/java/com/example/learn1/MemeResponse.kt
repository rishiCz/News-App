package com.example.learn1
import com.google.gson.annotations.SerializedName

data class MemeResponse(
    @SerializedName("author")
    val author: String, // FrostKnight96
    @SerializedName("nsfw")
    val nsfw: Boolean, // false
    @SerializedName("postLink")
    val postLink: String, // https://redd.it/z4t2fl
    @SerializedName("preview")
    val preview: List<String>,
    @SerializedName("spoiler")
    val spoiler: Boolean, // false
    @SerializedName("subreddit")
    val subreddit: String, // dankmemes
    @SerializedName("title")
    val title: String, // It's that time again...
    @SerializedName("ups")
    val ups: Int, // 39
    @SerializedName("url")
    val url: String // https://i.redd.it/qohcjgfww62a1.jpg
)