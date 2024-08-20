package com.example.myapplication.Retrofit

import com.example.myapplication.Data.Request.BabyCodeRequest
import com.example.myapplication.Data.Request.BabyRequest
import com.example.myapplication.Data.Request.FamilyRoleRequest
import com.example.myapplication.Data.Response.CommonResponse
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.POST

interface LoginIF {
    @POST("/oauth2/authorization/kakao")
    fun authorizeKakao(): Call<ResponseBody>

    @POST("/oauth2/authorization/google")
    fun authorizeGoogle(): Call<ResponseBody>

    @POST("/api/login/process-baby")
    fun postBabyCode(@Body babyCode: BabyCodeRequest): Call<CommonResponse>

    @POST("/api/login/process-baby")
    fun createNewBaby(@Body request: BabyRequest): Call<CommonResponse>

    @POST("/api/login/process-baby")
    fun participateFamily(@Body request: FamilyRoleRequest): Call<CommonResponse>

    @POST("/reissue")
    fun reissueToken(): Call<ResponseBody>
}