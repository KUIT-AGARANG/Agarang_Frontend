package com.example.myapplication.Data.Response

data class FirstQuestion(
    val id:String,
    val text: String,
    val audioUrl: String
)

data class FirstAnsResponse(
    val isSuccess: Boolean,
    val code: Int,
    val message: String,
    val result: FirstResult
)

data class FirstResult(
    val question: FirstQuestion
)

data class SelectMusicResponse(
    val isSuccess: Boolean,
    val code: Int,
    val message: String,
    val result: List<Any>
)
