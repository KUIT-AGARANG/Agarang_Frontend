package com.example.myapplication.Data.Response

data class DiaryDayResponse(
    val isSuccess: Boolean,
    val code: Int,
    val message: String,
    val result: DayResult
)
data class DayResult(
    val dailyMemories: List<DailyMemory>
)

data class DailyMemory(
    val id: Int,
    val date: String,
    val imageUrl: String
)