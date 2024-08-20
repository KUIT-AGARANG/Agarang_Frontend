package com.example.myapplication.Retrofit

import android.content.Context
import okhttp3.Cookie
import okhttp3.CookieJar
import okhttp3.HttpUrl
import okhttp3.HttpUrl.Companion.toHttpUrlOrNull

class PersistentCookieJar(private val context: Context) : CookieJar {
    private val cookieStore = HashMap<String, List<Cookie>>()

    // 요청에 따라 쿠키를 저장하는 메서드
    override fun saveFromResponse(url: HttpUrl, cookies: List<Cookie>) {
        cookieStore[url.host] = cookies
    }

    // 요청 시 쿠키를 불러오는 메서드
    override fun loadForRequest(url: HttpUrl): List<Cookie> {
        // SharedPreferences에서 토큰을 불러옴
        val sharedPreferences = context.getSharedPreferences("auth_prefs", Context.MODE_PRIVATE)
        val accessToken = sharedPreferences.getString("auth_token", null)
        val refreshToken = sharedPreferences.getString("refresh_token", null)

        // 쿠키를 구성
        if (accessToken != null && refreshToken != null) {
            val cookies = listOf(
                Cookie.Builder().name("ACCESS").value(accessToken).domain(url.host).build(),
                Cookie.Builder().name("REFRESH").value(refreshToken).domain(url.host).build()
            )
            return cookies
        }

        return cookieStore[url.host] ?: ArrayList()
    }

    // 직접 쿠키를 추가할 수 있는 메서드
    fun addCookie(url: String, cookieString: String) {
        val httpUrl = url.toHttpUrlOrNull() ?: return
        val cookie = Cookie.parse(httpUrl, cookieString) ?: return
        val cookies = (cookieStore[httpUrl.host] ?: ArrayList()).toMutableList()
        cookies.add(cookie)
        cookieStore[httpUrl.host] = cookies
    }
}
