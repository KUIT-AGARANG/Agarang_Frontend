package com.example.myapplication.Data.Response

import com.example.myapplication.Data.Response.HomeResponse.Result

data class HomeChildResponse(
    val isSuccess: Boolean,
    val code: Int,
    val message: String,
    val result: Result
) {
    data class Result(
        val babyName: String,
        val dueDate: String,
        val weight: Float?
    )
}
