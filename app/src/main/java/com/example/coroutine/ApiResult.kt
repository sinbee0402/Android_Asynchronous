package com.example.coroutine

data class ApiResult(
    val total: Int,
    val totalHits: Int,
    val hits: MutableList<ResultImage>
)