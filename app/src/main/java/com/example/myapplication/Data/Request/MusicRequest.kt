package com.example.myapplication.Data.Request

//음악 즐겨찾기 설정
data class MusicBookmark(
    val memoryId:Int
)

//플레이리스트 음악 삭제
data class MusicDelete(
    val playlistId:Long,
    val memoryId:Int
)