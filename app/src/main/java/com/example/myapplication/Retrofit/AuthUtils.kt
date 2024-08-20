package com.example.myapplication.Retrofit

import android.content.Context


object AuthUtils {
    //저장된 토큰 가져오는 함수
    fun getAuthToken(context: Context): String? {
        val sharedPreferences = context.getSharedPreferences("auth_prefs", Context.MODE_PRIVATE)
        return sharedPreferences.getString("auth_token", null)
    }
}