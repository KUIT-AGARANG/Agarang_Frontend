package com.example.myapplication.Data.Response

data class HomeResponse(
    val isSuccess: Boolean,
    val code: Int,
    val message: String,
    val result: Result
) {
    data class Result(
        val today: String,
        val babyName: String,
        val characterUrl: String,
        val speechBubble: String,
        val memories: List<Memory>,
        val dday: Int
    )

    data class Memory(
        val id: Int,
        val imageUrl: String
    )
}