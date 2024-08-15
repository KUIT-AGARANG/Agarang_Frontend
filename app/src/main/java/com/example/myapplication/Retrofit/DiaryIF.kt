package com.example.myapplication.Retrofit

import com.example.myapplication.Data.Request.EditMemoryRequest
import com.example.myapplication.Data.Request.bookmarkSetRequest
import com.example.myapplication.Data.Response.BookmarkSetResult
import com.example.myapplication.Data.Response.DeleteDiaryResponse
import com.example.myapplication.Data.Response.DiaryBookmarkResponse
import com.example.myapplication.Data.Response.DiaryCardResponse
import com.example.myapplication.Data.Response.DiaryDayResponse
import com.example.myapplication.Data.Response.DiaryMonthResponse
import com.example.myapplication.Data.Response.EditMemoryResponse
import retrofit2.http.GET
import retrofit2.http.Query
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.HTTP
import retrofit2.http.POST
import retrofit2.http.PUT

interface DiaryIF {
    @GET("api/memory")
    fun getMonthlyMemories(@Query("viewType") viewType: String, @Query("date") date:String): Call<DiaryMonthResponse>

    @GET("api/memory")
    fun getDailyMemories(@Query("viewType") viewType: String, @Query("date") date:String): Call<DiaryDayResponse>

    @GET("api/memory")
    fun getBookmarkedMemories(@Query("viewType") viewType: String): Call<DiaryBookmarkResponse>


    //카드 뷰를 date로 요청
    @GET("api/memory")
    fun getCardViewByDate(@Query("viewType") viewType: String, @Query("date") date: String): Call<DiaryCardResponse>

    // 카드 뷰를 ID로 요청
    @GET("api/memory")
    fun getCardViewById(@Query("viewType") viewType: String, @Query("id") id: String): Call<DiaryCardResponse>

    @POST("api/memory/bookmark")
    fun sendBookmarkSet(@Body memoryId: bookmarkSetRequest): Call <BookmarkSetResult>


    /*@DELETE("/api/memory")
    fun deleteDiary(@Body memoryId: bookmarkSetRequest): Call <BookmarkDeleteDiary>*/

    @HTTP(method = "DELETE", path = "/api/memory", hasBody = true)
    fun deleteDiary(@Body memoryId: bookmarkSetRequest): Call<DeleteDiaryResponse>

    @PUT("api/memory")
    fun editMemory(@Body request: EditMemoryRequest): Call<EditMemoryResponse>

}