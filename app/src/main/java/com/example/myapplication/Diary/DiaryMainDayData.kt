package com.example.myapplication.Diary

import java.io.Serializable

data class DiaryMainDayData(
    val imageResId: String="",
    var id: Int = 0,
    val date: String,
    val isPlaceholder: Boolean = false // 데이터가 없는 날을 위한 속성
) : Serializable {
    val year: Int
    val month: Int
    val day: Int

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

