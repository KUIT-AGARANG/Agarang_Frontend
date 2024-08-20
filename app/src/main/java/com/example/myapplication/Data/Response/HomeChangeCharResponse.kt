package com.example.myapplication.Data.Response

data class HomeChangeCharResponse(
    val isSuccess: Boolean,
    val code: Int,
    val message: String,
    val result: List<Character>
){
    data class Character(
        val characterId: Int,
        val name: String,
        val description: String,
        val imageUrl: String
    )
}
