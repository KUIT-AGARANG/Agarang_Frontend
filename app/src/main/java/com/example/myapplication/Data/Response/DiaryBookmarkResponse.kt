package com.example.myapplication.Data.Response

data class DiaryBookmarkResponse(
    val isSuccess: Boolean,
    val code: Int,
    val message: String,
    val result: BookmarkResult
)

data class BookmarkResult(
    val memoires: List<BookmarkMemory>
)

data class BookmarkMemory(
    val id: Int,
    val imageUrl: String
)

data class BookmarkSetResult(
    val isSuccess: Boolean,
    val code: Int,
    val message: String,
    val result: List<Any>
)


