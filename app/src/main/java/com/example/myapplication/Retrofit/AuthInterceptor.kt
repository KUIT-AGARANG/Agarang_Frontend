package com.example.myapplication.Retrofit

import android.content.Context
import android.util.Log
import okhttp3.Cookie
import okhttp3.HttpUrl
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Response
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class AuthInterceptor(
    private val context: Context, private val onTokenExpired: () -> Unit) : Interceptor {
    private val cookieJar = PersistentCookieJar(context)

    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()
        val originalResponse = chain.proceed(request)

        if (originalResponse.code == 4018 || !originalResponse.isSuccessful) {
            val responseBody = originalResponse.peekBody(Long.MAX_VALUE).string()
            if (responseBody.contains("액세스 토큰이 만료되었습니다")) {
                val newTokens = refreshToken(request.url)
                if (newTokens != null) {
                    val newRequest = request.newBuilder()
                        .header("Authorization", "${newTokens.first}")
                        .build()

                    originalResponse.close()  // 기존 응답 닫기
                    return chain.proceed(newRequest)
                }
            }
        }
        return originalResponse
    }

    private fun refreshToken(url: HttpUrl): Pair<String, String>? {
        val refreshToken = getRefreshTokenFromSharedPreferences()
        if (refreshToken.isNullOrEmpty()) {
            Log.e("AuthInterceptor", "Refresh token is null or empty")
            return null
        }

        val loggingInterceptor = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }

        val client = OkHttpClient.Builder()
            .cookieJar(cookieJar)
            .addInterceptor(loggingInterceptor)
            .build()

        val retrofit = Retrofit.Builder()
            .baseUrl("https://www.agarang.site")
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        val service = retrofit.create(LoginIF::class.java)

        try {
            val response = service.reissueToken().execute()
            if (response.isSuccessful) {

                val cookies = response.headers().values("Set-Cookie")
                val accessToken = extractToken(cookies, "ACCESS")
                val newRefreshToken = extractToken(cookies, "REFRESH")

                Log.d("AuthInterceptor", "Extracted ACCESS token: $accessToken")
                Log.d("AuthInterceptor", "Extracted REFRESH token: $newRefreshToken")

                if (accessToken != null && newRefreshToken != null) {
                    saveTokens(url, accessToken, newRefreshToken)
                    return Pair(accessToken, newRefreshToken)
                }
            } else{
                val errorBody = response.errorBody()?.string()
                Log.e("AuthInterceptor", "Token refresh failed $errorBody")

                // Check for specific error code (4016)
                if (errorBody?.contains("\"code\":4016") == true) {
                    Log.e("AuthInterceptor", "Refresh token has expired. Re-login required.")
                    onTokenExpired()  // Trigger onTokenExpired callback
                    return null
                } else if (errorBody?.contains("액세스 토큰이 만료되었습니다") == true) {
                    onTokenExpired()
                    return null
                } else if (errorBody?.contains("\"code\":4019") == true) {
                    Log.e("AuthInterceptor", "Refresh token has expired. Re-login required.")
                    onTokenExpired()  // Trigger onTokenExpired callback
                    return null
                }
            }
        } catch (e: Exception) {
            Log.e("AuthInterceptor", "Token refresh failed", e)
        }
        return null
    }

    private fun getRefreshTokenFromSharedPreferences(): String? {
        val sharedPreferences = context.getSharedPreferences("auth_prefs", Context.MODE_PRIVATE)
        return sharedPreferences.getString("refresh_token", null)
    }

    private fun extractToken(cookies: List<String>, tokenName: String): String? {
        return cookies.firstOrNull { it.startsWith("$tokenName=") }
            ?.substringAfter("$tokenName=")
            ?.substringBefore(";")
    }

    private fun saveTokens(url: HttpUrl, accessToken: String, refreshToken: String) {
        val sharedPreferences = context.getSharedPreferences("auth_prefs", Context.MODE_PRIVATE)
        sharedPreferences.edit().apply {
            putString("auth_token", accessToken)
            putString("refresh_token", refreshToken)
            apply()
        }
        cookieJar.saveFromResponse(url, listOf(
            Cookie.Builder().name("ACCESS").value(accessToken).domain(url.host).build(),
            Cookie.Builder().name("REFRESH").value(refreshToken).domain(url.host).build()
        ))
        Log.d("AuthInterceptor", "Saved ACCESS token: $accessToken")
        Log.d("AuthInterceptor", "Saved REFRESH token: $refreshToken")
    }

    interface AuthCallback {
        fun onTokenExpired()
    }
}