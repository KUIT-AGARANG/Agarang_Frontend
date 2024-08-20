package com.example.myapplication.Retrofit

import android.content.Context
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.security.cert.X509Certificate
import java.util.concurrent.TimeUnit
import javax.net.ssl.SSLContext
import javax.net.ssl.SSLSocketFactory
import javax.net.ssl.TrustManager
import javax.net.ssl.X509TrustManager

object RetrofitService {
    private const val BASE_URL = "https://www.agarang.site/"
    private lateinit var cookieJar: PersistentCookieJar
    private var authCallback: AuthInterceptor.AuthCallback? = null

    fun setAuthCallback(callback: AuthInterceptor.AuthCallback) {
        authCallback = callback
    }

    private fun createOkHttpClient(context: Context): OkHttpClient {
        cookieJar = PersistentCookieJar(context)
        return OkHttpClient.Builder()
            .addInterceptor(HttpLoggingInterceptor().apply {
                level = HttpLoggingInterceptor.Level.BODY
            })
            .addInterceptor(AuthInterceptor(context) { authCallback?.onTokenExpired() })
            .cookieJar(cookieJar)
            .hostnameVerifier { _, _ -> true }
            .sslSocketFactory(TrustAllCerts.createSSLSocketFactory(), TrustAllCerts)
            .build()
    }

    fun createRetrofit(context: Context): Retrofit {
        return Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(createOkHttpClient(context))
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    fun addCookie(url: String, cookieString: String) {
        cookieJar.addCookie(url, cookieString)
    }
}

// 신뢰할 수 있는 모든 SSL 인증서 처리
object TrustAllCerts : X509TrustManager {
    override fun checkClientTrusted(chain: Array<out X509Certificate>?, authType: String?) {}
    override fun checkServerTrusted(chain: Array<out X509Certificate>?, authType: String?) {}
    override fun getAcceptedIssuers(): Array<X509Certificate> = arrayOf()

    fun createSSLSocketFactory(): SSLSocketFactory {
        val sslContext = SSLContext.getInstance("TLS")
        sslContext.init(null, arrayOf<TrustManager>(this), null)
        return sslContext.socketFactory
    }
}

