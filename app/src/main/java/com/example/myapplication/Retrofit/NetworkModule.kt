package com.example.myapplication.Retrofit

import android.content.Context
import com.example.myapplication.R
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import java.security.KeyStore
import java.security.cert.CertificateFactory
import java.security.cert.X509Certificate
import javax.net.ssl.SSLContext
import javax.net.ssl.TrustManagerFactory
import javax.net.ssl.X509TrustManager

object NetworkModule {
    private lateinit var tmf: TrustManagerFactory
    private lateinit var sslContext: SSLContext

    fun initialize(context: Context) {
        createSSLContext(context)
    }

    fun provideOkHttpClient(): OkHttpClient {
        val httpLoggingInterceptor = HttpLoggingInterceptor()
            .setLevel(HttpLoggingInterceptor.Level.BODY)

        return OkHttpClient.Builder()
            .sslSocketFactory(
                sslContext.socketFactory,
                tmf.trustManagers[0] as X509TrustManager
            )
            .addInterceptor(httpLoggingInterceptor)
            .build()
    }

    private fun createSSLContext(context: Context) {
        try {
            val cf = CertificateFactory.getInstance("X.509")
            context.resources.openRawResource(R.raw.agarang).use { caInput ->
                val ca = cf.generateCertificate(caInput)
                println("ca = ${(ca as X509Certificate).subjectDN}")

                val keyStore = KeyStore.getInstance(KeyStore.getDefaultType()).apply {
                    load(null, null)
                    setCertificateEntry("ca", ca)
                }

                val tmfAlgorithm = TrustManagerFactory.getDefaultAlgorithm()
                tmf = TrustManagerFactory.getInstance(tmfAlgorithm).apply {
                    init(keyStore)
                }

                sslContext = SSLContext.getInstance("TLS").apply {
                    init(null, tmf.trustManagers, java.security.SecureRandom())
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
            throw RuntimeException("SSL 컨텍스트 초기화 실패", e)
        }
    }
}