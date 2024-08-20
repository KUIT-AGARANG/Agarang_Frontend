package com.example.myapplication.Data.Response

data class DiaryMonthResponse(
    val isSuccess: Boolean,
    val code: Int,
    val message: String,
    val result: MonthResult
)

data class MonthResult(
    val monthlyMemories: List<MonthlyMemory>
)

data class MonthlyMemory(
    val date: String,
    val imageUrl: String
)