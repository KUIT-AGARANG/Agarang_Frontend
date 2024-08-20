package com.example.myapplication.Login

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.CookieManager
import android.webkit.WebResourceError
import android.webkit.WebResourceRequest
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.Toast
import com.example.myapplication.MainActivity
import com.example.myapplication.Memory.MemoryMainActivity
import com.example.myapplication.R
import com.example.myapplication.Retrofit.AuthInterceptor
import com.example.myapplication.Retrofit.RetrofitService
import com.example.myapplication.databinding.FragmentLoginStartBinding


class LoginStartFragment : Fragment(), AuthInterceptor.AuthCallback {

    private lateinit var binding: FragmentLoginStartBinding
    private lateinit var cookieManager: CookieManager


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentLoginStartBinding.inflate(inflater, container, false)

        // 저장된 토큰 확인
        val sharedPreferences = requireActivity().getSharedPreferences("auth_prefs", Context.MODE_PRIVATE)
        val savedToken = sharedPreferences.getString("auth_token", null)
        Log.d("ACCESS", "$savedToken")
        RetrofitService.setAuthCallback(this)

        if (savedToken != null) {
            // 토큰이 존재하면 메인 액티비티로 이동
            val intent = Intent(activity, MainActivity::class.java)
            startActivity(intent)

            return binding.root
        }

        binding.icLoginKakao.setOnClickListener {
            authorizeKakao()
        }

        binding.icLoginGoogle.setOnClickListener {
            authorizeGoogle()
        }

        // WebView 설정
        binding.webView.settings.javaScriptEnabled = true
        cookieManager = CookieManager.getInstance() // CookieManager 인스턴스 가져오기
        cookieManager.setAcceptCookie(true)

        binding.webView.webViewClient = object : WebViewClient() {
            override fun shouldOverrideUrlLoading(view: WebView?, url: String?): Boolean {
                Log.d("WebView", "Loading URL: $url")
                url?.let {

                    if (it == "https://www.agarang.site/api/login/success") {
                        // 로그인 성공 후 리다이렉트된 URL 감지
                        handleLoginSuccess(it)
                        return true
                    }
                }
                val cookies = cookieManager.getCookie(url)
                Log.d("쿠키 확인", "Received cookies: $cookies")
                return false
            }

            override fun onReceivedError(view: WebView?, request: WebResourceRequest?, error: WebResourceError?) {
                Log.e("WebView", "Error: ${error?.description}, Code: ${error?.errorCode}")
                super.onReceivedError(view, request, error)
            }
        }

        return binding.root
    }

    private fun authorizeGoogle() {
        binding.webView.getSettings().setUserAgentString("Mozilla/5.0 AppleWebKit/535.19 Chrome/56.0.0 Mobile Safari/535.19");

        activity?.runOnUiThread {
            binding.webView.visibility = View.VISIBLE
            binding.webView.loadUrl("https://www.agarang.site/oauth2/authorization/google")
        }
    }

    private fun authorizeKakao() {

        binding.webView.visibility = View.VISIBLE
        binding.webView.loadUrl("https://www.agarang.site/oauth2/authorization/kakao")
       /* val loginService = RetrofitService.createRetrofit(requireContext()).create(LoginIF::class.java)

        loginService.authorizeKakao().enqueue(object : Callback<ResponseBody> {
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                if (response.isSuccessful) {
                    val loginUrl = response.raw().request.url.toString()
                    activity?.runOnUiThread {
                        binding.webView.visibility = View.VISIBLE
                        binding.webView.loadUrl(loginUrl)
                    }
                    *//*val cookies = response.headers()["Set-Cookie"]

                    val authToken = extractAuthToken(cookies)
                    if (authToken != null) {
                        saveAuthToken(authToken)
                        Log.d("토큰 확인", "Received JWT: $authToken")
                    } else {
                        Log.e("토큰 에러", "Authorization token not found in cookies")
                    }*//*
                } else {
                    Toast.makeText(context, "에러: ${response.code()}", Toast.LENGTH_SHORT).show()
                }
            }
            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                Toast.makeText(context, "네트워크 오류: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })*/
    }

    private fun handleLoginSuccess(url: String) {
        binding.webView.visibility = View.GONE

        val cookies = cookieManager.getCookie(url)
        val (accessToken, refreshToken) = extractTokens(cookies)

        if (accessToken != null && refreshToken != null) {
            saveAuthToken(accessToken, refreshToken)
            Log.d("토큰 확인", "Received ACCESS: $accessToken, REFRESH: $refreshToken")

            // 쿠키를 CookieJar에 추가
            RetrofitService.createRetrofit(requireContext())
            RetrofitService.addCookie(url, "ACCESS=$accessToken")
            RetrofitService.addCookie(url, "REFRESH=$refreshToken")

        } else {
            Log.e("토큰 에러", "Authorization tokens not found in cookies: $cookies")
        }

        Toast.makeText(context, "로그인 성공!", Toast.LENGTH_SHORT).show()

        activity?.supportFragmentManager?.beginTransaction()
            ?.replace(R.id.main_frm2, LoginCode1Fragment())
            ?.commit()
    }


    private fun extractTokens(cookies: String?): Pair<String?, String?> {
        val accessToken = cookies?.split(";")?.find { it.trim().startsWith("ACCESS=") }?.substringAfter("=")
        val refreshToken = cookies?.split(";")?.find { it.trim().startsWith("REFRESH=") }?.substringAfter("=")
        return Pair(accessToken, refreshToken)
    }

    private fun saveAuthToken(accessToken: String, refreshToken: String) {
        val sharedPreferences = requireActivity().getSharedPreferences("auth_prefs", Context.MODE_PRIVATE)
        sharedPreferences.edit().apply {
            putString("auth_token", accessToken) // ACCESS 토큰 저장
            putString("refresh_token", refreshToken) // REFRESH 토큰 저장
            apply()
        }
        Log.d("토큰 저장", "ACCESS token saved: $accessToken, REFRESH token saved: $refreshToken")
    }

    override fun onTokenExpired() {
        activity?.runOnUiThread {
            // 저장된 토큰 삭제
            val sharedPreferences = requireActivity().getSharedPreferences("auth_prefs", Context.MODE_PRIVATE)
            sharedPreferences.edit().clear().apply()

            // 웹뷰 쿠키 삭제
            CookieManager.getInstance().removeAllCookies(null)
            CookieManager.getInstance().flush()

            // 로그인 화면으로 이동
            Toast.makeText(context, "세션이 만료되었습니다. 다시 로그인해주세요.", Toast.LENGTH_LONG).show()

            // 로그인 액티비티로 이동
            val intent = Intent(activity, LoginActivity::class.java)
            startActivity(intent)
            activity?.finish()
        }
    }
}