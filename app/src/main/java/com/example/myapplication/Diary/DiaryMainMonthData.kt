package com.example.myapplication.Diary

import android.util.Log
import java.io.Serializable


data class DiaryMainMonthData(
    val imageUrl: String,
    val date: String
) : Serializable, Comparable<DiaryMainMonthData> {
    var year: Int = 0
    var month: Int = 0
    var day: Int = 1

    init {
        val parts = date.split("-").map { it.trim() }
        if (parts.size >= 2) {
            year = parts[0].toIntOrNull() ?: 0
            month = parts[1].toIntOrNull() ?: 0
        } else {
            Log.e("DiaryMainMonthData", "Invalid date format: $date")
        }
    }

    override fun compareTo(other: DiaryMainMonthData): Int {
        return when {
            this.year != other.year -> this.year.compareTo(other.year)
            else -> this.month.compareTo(other.month)
        }
    }
}