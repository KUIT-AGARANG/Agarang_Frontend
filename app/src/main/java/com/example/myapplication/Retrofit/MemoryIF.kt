package com.example.myapplication.Retrofit

import com.example.myapplication.Data.Request.Memory2Request
import com.example.myapplication.Data.Response.Memory2Response
import com.example.myapplication.Data.Request.FirstAnsRequest
import com.example.myapplication.Data.Request.selectMusicRequest
import com.example.myapplication.Data.Response.FirstAnsResponse
import com.example.myapplication.Data.Response.MemoryImageToQuestionResponse
import com.example.myapplication.Data.Response.SelectMusicResponse
import okhttp3.MultipartBody
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part

interface MemoryIF {
    @POST("/api/memory/ai/first-ans")
    fun sendFirstAns(@Body result:FirstAnsRequest): Call<FirstAnsResponse>

    @Multipart
    @POST("/api/memory/ai/image-to-question")
    fun sendImageToQuestion(@Part image: MultipartBody.Part): Call<MemoryImageToQuestionResponse>

    @POST("/api/memory/ai/second-ans")
    fun sendMemoryDetails(@Body request: Memory2Request): Call<Memory2Response>

    @POST("/api/memory/ai/music")
    fun sendSelectMusic(@Body request: selectMusicRequest):Call<SelectMusicResponse>
}