package com.example.myapplication.Data.Response

data class Memory2Response(
    val isSuccess: Boolean,
    val code: Int,
    val message: String,
    val result: List<Any> = emptyList()
)
