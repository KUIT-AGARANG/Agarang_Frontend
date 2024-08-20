package com.example.myapplication.Data.Request

data class FirstAnsRequest(
    val id:String,
    val text: String
)
data class bookmarkSetRequest(
    val memoryId: Long
)
data class selectMusicRequest(
    val id:String,
    val musicChoice: MusicChoice
)
data class MusicChoice(
    val instrument:String,
    val genre:String,
    val mood:String,
    val tempo:String
)
data class EditMemoryRequest(
    val memoryId: Long,
    val text: String
)