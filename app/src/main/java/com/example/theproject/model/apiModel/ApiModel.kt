package com.example.theproject.model.apiModel

data class ApiModel(
    val products: List<Product>,
    val total: Int,
    val skip: Int,
    val limit: Int
)