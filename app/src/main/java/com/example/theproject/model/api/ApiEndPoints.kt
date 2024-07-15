package com.example.theproject.model.api

import com.example.theproject.model.apiModel.ApiModel
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface ApiEndPoints {
    @GET("products")
    suspend fun getProducts(): Call<ApiModel>

    @GET("products/search")
    suspend fun searchProducts(@Query("q") query: String): ApiModel
}