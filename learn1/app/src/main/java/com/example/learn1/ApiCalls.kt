package com.example.learn1

import com.example.learn1.DataClass.DataNewsList
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Url

interface ApiCalls {
    @GET("/v2//top-headlines?country=in&apiKey=e4d8cf10d30147dd9f8c8a39981315e1")
    suspend fun getNewsResponse() : Response<DataNewsList>
    @GET
    suspend fun getDynamicNews( @Url Url: String) : Response<DataNewsList>
}