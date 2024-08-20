package com.example.myapplication.Retrofit


import com.example.myapplication.Data.Request.CharacterUpdateRequest
import com.example.myapplication.Data.Request.ChildInfoUpdateRequest

import com.example.myapplication.Data.Response.FamilyResponse

import com.example.myapplication.Data.Response.HomeChangeCharResponse
import com.example.myapplication.Data.Response.HomeCharUpdateResponse
import com.example.myapplication.Data.Response.HomeChildResponse
import com.example.myapplication.Data.Response.HomeChildUpdateResponse
import com.example.myapplication.Data.Response.HomeResponse
import com.example.myapplication.Data.Response.HomeSettingResponse
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.PATCH

interface HomeIF {
    //홈 메인
    @GET("/api/home")
    fun getHomeData(): Call<HomeResponse>

    //홈 설정
    @GET("api/home/setting")
    fun getSettingData(): Call<HomeSettingResponse>

    //아이 정보 조회
    @GET("/api/home/setting/baby")
    fun getChildInfoData(): Call<HomeChildResponse>

    //아이 정보 수정
    @PATCH("/api/home/setting/baby")
    fun updateChildInfo(@Body requestBody: ChildInfoUpdateRequest): Call<HomeChildUpdateResponse>

    //홈 캐릭터 조회
    @GET("api/home/setting/character")
    fun getCharacterData(): Call<HomeChangeCharResponse>

    //홈 캐릭터 수정
    @PATCH("/api/home/setting/character")
    fun updateCharacter(@Body requestBody: CharacterUpdateRequest): Call<HomeCharUpdateResponse>

    //홈 가족정보 조회
    @GET("/api/home/setting/family")
    fun getFamilyInfo(): Call<FamilyResponse>

}