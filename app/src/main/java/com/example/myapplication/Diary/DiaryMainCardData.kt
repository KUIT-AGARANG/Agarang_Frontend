package com.example.myapplication.Diary

import android.util.Log
import java.io.Serializable

data class DiaryMainCardData(
    var id: Int = 0,
    val writer: String,
    val date: String,
    val content: String?,
    val musicUrl: String, // 음악 재생을 위한 URL (필요한 경우)
    val imageUrl: String,
    val hashTags: List<String>,
    var bookmarked: Boolean,
    val musicTitle: String
) : Serializable {
    var year: Int = 0
    var month: Int = 0
    var day: Int = 0

    val formattedHashtags: List<String>
        get() = hashTags.take(3).map { "#$it" } // 최대 3개의 해시태그만 사용

    init {
        if (date.length == 8) {
            val yearPart = date.substring(0, 4).toIntOrNull()
            val monthPart = date.substring(4, 6).toIntOrNull()
            val dayPart = date.substring(6, 8).toIntOrNull()

            if (yearPart != null && monthPart != null && dayPart != null) {
                year = yearPart
                month = monthPart
                day = dayPart
            } else {
                throw IllegalArgumentException("Invalid date format: $date")
            }
        } else {
            throw IllegalArgumentException("Invalid date format: $date")
        }
    }
}
