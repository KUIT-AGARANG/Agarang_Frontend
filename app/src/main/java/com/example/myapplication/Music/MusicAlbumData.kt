package com.example.myapplication.Music

import android.os.Parcel
import android.os.Parcelable
import androidx.versionedparcelable.VersionedParcelize
import kotlinx.parcelize.Parcelize

@Parcelize
data class MusicAlbumData(
    val musicImgId:Int,
    val musicTitle:String,
    val musicTag1:String,
    val musicTag2:String
):Parcelable
