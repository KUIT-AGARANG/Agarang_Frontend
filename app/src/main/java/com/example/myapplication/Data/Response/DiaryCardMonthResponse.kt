package com.example.myapplication.Data.Response

data class DiaryCardMonthResponse(
    val isSuccess: Boolean,
    val code: Int,
    val message: String,
    val result: MemoryResult
)


data class MemoryResult(
    val memories: List<CardMemory>
)

data class DiaryCardResponse(
    val isSuccess: Boolean,
    val code: Int,
    val message: String,
    val result: CardMemory
)


data class CardMemory(
    var id: Int = 0,
    val writer: String,
    val date: String,
    val content: String,
    val musicUrl: String,
    val imageUrl: String,
    val hashTags: List<String>,
    val bookmarked: Boolean,
    val musicTitle: String = ""
)
