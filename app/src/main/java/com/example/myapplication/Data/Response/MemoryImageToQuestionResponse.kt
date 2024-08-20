package com.example.myapplication.Data.Response

// 응답 데이터 클래스
data class MemoryImageToQuestionResponse(
    val isSuccess: Boolean,
    val code: Int,
    val message: String,
    val result: ImageResult
)

data class ImageResult(
    val question: ImageQuestion
)

data class ImageQuestion(
    val id: String,
    val text: String,
    val audioUrl: String
)
