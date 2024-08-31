package com.example.myapplication.Music

import android.os.Parcel
import android.os.Parcelable
import androidx.versionedparcelable.VersionedParcelize
import kotlinx.parcelize.Parcelize

@Parcelize
data class MusicAlbumData(
    val memoryId:Int,
    val imageUrl:String,
    val musicTitle:String,
    val musicUrl:String = "",
    val musicTag1:String,
    val musicTag2:String,
    var bookmarked: Boolean
):Parcelable
