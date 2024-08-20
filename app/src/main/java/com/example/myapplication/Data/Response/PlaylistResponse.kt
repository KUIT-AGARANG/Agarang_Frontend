package com.example.myapplication.Data.Response

//플레이리스트 전체 조회
data class AllPlaylistResponse(
    val code:Int,
    val status:Int,
    val message:String,
    val result:AllPlaylistResult
)

data class AllPlaylistResult(
    val playlists:List<Playlist>
)

data class Playlist(
    val id:Int,
    val name: String,
    val imageUrl:String
)

//플레이 리스트 별 트랙 조회
data class TrackResponse(
    val isSuccess:Boolean,
    val code: Int,
    val message: String,
    val result:TrackResult
)

data class TrackResult(
    val playlists: Playlist,
    val tracks:List<Tracks>,
    val totalTrackCount:Int,
    val totalTrackTime:Int
)

data class Tracks(
    val memoryId:Int,
    val imageUrl: String,
    val musicTitle:String,
    val musicUrl:String,
    val hashTags:List<String>,
    val bookmarked:Boolean
)

//음악 즐겨찾기 설정
data class MusicBookmarkResponse(
    val isSuccess:Boolean,
    val code: Int,
    val message: String,
    val result:List<Any>
)

//음악 삭제 설정
data class MusicDeleteResponse(
    val isSuccess:Boolean,
    val code: Int,
    val message: String,
    val result:List<Any>
)


