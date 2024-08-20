package com.example.myapplication.Retrofit

import com.example.myapplication.Data.Request.MusicBookmark
import com.example.myapplication.Data.Request.MusicDelete
import com.example.myapplication.Data.Request.bookmarkSetRequest
import com.example.myapplication.Data.Response.AllPlaylistResponse
import com.example.myapplication.Data.Response.BookmarkSetResult
import com.example.myapplication.Data.Response.MusicBookmarkResponse
import com.example.myapplication.Data.Response.MusicDeleteResponse
import com.example.myapplication.Data.Response.TrackResponse
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.HTTP
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

interface PlaylistIF {
    @GET("api/playlists")
    fun getAllPlaylist(): Call<AllPlaylistResponse>

    @GET("api/playlists/{playlistId}/track")
    fun getTracklist(
        @Path("playlistId") playlistId:Long
    ): Call<TrackResponse>

    @POST("/api/playlists/bookmark")
    fun sendMusicBookmark(
        @Body memoryId: MusicBookmark
    ): Call<MusicBookmarkResponse>

    @HTTP(method = "DELETE", path = "/api/playlists/music/delete", hasBody = true)
    fun sendMusicDelete(
        @Body deleteRequest: MusicDelete
    ): Call<MusicDeleteResponse>


}