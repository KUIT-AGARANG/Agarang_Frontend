package com.example.myapplication.Data

import android.app.Application
import android.content.Context
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.myapplication.Music.MusicAlbumData

// UI 관련 데이터를 저장하고 관리해주는 아키텍처

class SharedViewModel(application:Application) : AndroidViewModel(application) {

    val sharedPreferences = application.getSharedPreferences("app_prefs",Context.MODE_PRIVATE)

    // 아이 정보를 위한 데이터 입니다.
    // 1. 태명 2. 예정일 3. 체중 4. 가족 코드 5. 가족 역할
    private val _babyName=MutableLiveData<String>()
    val babyName: LiveData<String> get() =_babyName

    private val _dueDate=MutableLiveData<String>()
    val dueDate: LiveData<String> get() =_dueDate

    private val _babyWeight=MutableLiveData<String>()
    val babyWeight: LiveData<String> get() =_babyWeight

    private val _familyCode=MutableLiveData<String>()
    val familyCode: LiveData<String> get() =_familyCode

    private val _familyRole=MutableLiveData<String>()
    val familyRole: LiveData<String> get() =_familyRole

    private val _babyDday=MutableLiveData<Int>()
    val babyDday: LiveData<Int> get() =_babyDday

    fun setBabyName(babyName:String){
        _babyName.value=babyName
        sharedPreferences.edit().putString("babyName",babyName).apply()
    }

    fun setDueDate(dueDate:String){
        _dueDate.value=dueDate
        sharedPreferences.edit().putString("dueDate",dueDate).apply()
    }

    fun setBabyWeight(babyWeight:String){
        _babyWeight.value=babyWeight
        sharedPreferences.edit().putString("babyWeight",babyWeight).apply()
    }

    fun setFamilyCode(familyCode:String){
        _familyCode.value=familyCode
        sharedPreferences.edit().putString("familyCode",familyCode).apply()
    }

    fun setFamilyRole(familyRole:String){
        _familyRole.value=familyRole
        sharedPreferences.edit().putString("familyRole",familyRole).apply()
    }

    fun setBabyDDay(babyDday:Int){
        _babyDday.value=babyDday
        sharedPreferences.edit().putInt("babyDday",babyDday).apply()
    }

    init {
        _babyName.value=sharedPreferences.getString("babyName","")
        _dueDate.value=sharedPreferences.getString("dueDate","")
        _babyWeight.value=sharedPreferences.getString("babyWeight","")
        _familyCode.value=sharedPreferences.getString("familyCode","")
        _familyRole.value=sharedPreferences.getString("familyRole","")
        _babyDday.value=sharedPreferences.getInt("babyDday",0)
    }

    // 음악 선정을 위한 데이터입니다.
    private val _instrument=MutableLiveData<String>()
    val instrument: LiveData<String> get() =_instrument //only read

    private val _genre=MutableLiveData<String>()
    val genre: LiveData<String> get() =_genre

    private val _mood=MutableLiveData<String>()
    val mood: LiveData<String> get() =_mood

    private val _tempo=MutableLiveData<String>()
    val tempo: LiveData<String> get() =_tempo

    fun setInstrument(instrument:String){
        _instrument.value=instrument.uppercase()
    }

    fun setGenre(genre:String){
        _genre.value=genre.uppercase()
    }

    fun setMood(mood:String){
        _mood.value=mood.uppercase()
    }

    fun setTempo(tempo:String){
        _tempo.value=tempo.uppercase()
    }

    // 홈에 음악을 연결하기 위한 데이터 입니다.
    private val _currentTrack=MutableLiveData<MusicAlbumData?>()
    val currentTrack:LiveData<MusicAlbumData?> = _currentTrack

    fun setCurrentTrack(track:MusicAlbumData){
        _currentTrack.value=track
    }
}