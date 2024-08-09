package com.example.myapplication.Diary

import java.io.Serializable


data class DiaryMainDayData(
    val writer: String="Unknown",
    var favorite: Boolean=false,
    val hashTags: List<String> = emptyList(),
    val imageResId: String="",

    val id: Int=0,
    val date: String,
    /*var bookmark: Int = 0,*/
    val content: String = "오늘 엄마랑 대관령에 다녀왔단다.오똑숲에서 많은 나무와 아름다운 할미 꽃을 보며 정말 행복했어. 자연 속에서 느낀 피톤치드 덕분에 마음이 한결 편안해졌단다. 우리 아가도 나중에 꼭 같이 가보자. 사랑해. 우리 아가!",
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

