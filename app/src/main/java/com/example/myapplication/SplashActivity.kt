package com.example.myapplication

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.myapplication.Login.LoginActivity
import com.example.myapplication.Onboarding.OnboardingActivity

class SplashActivity : AppCompatActivity() {
    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_splash)

        // View에 WindowInsets를 적용
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        //첫 실행 여부 확인 코드
        val sharedPreferences = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val isFirstRun = sharedPreferences.getBoolean(KEY_FIRST_RUN, true)

        // DURATION 후에 MainActivity로 이동
        Handler(Looper.getMainLooper()).postDelayed({
            /*val intent = Intent(this, OnboardingActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION)
            startActivity(intent)*/
            if (isFirstRun) {
                // 첫 실행일 경우 OnboardingActivity로 전환
                val intent = Intent(this, OnboardingActivity::class.java)
                startActivity(intent)

                // SharedPreferences에 값 저장 (첫실행 끝남)
                sharedPreferences.edit().putBoolean(KEY_FIRST_RUN, false).apply()
            } else {
                // 첫 실행이 아닐 경우 LoginActivity로 전환
                val intent = Intent(this, LoginActivity::class.java)
                startActivity(intent)
            }
            finish()
        }, DURATION)
    }

    companion object {
        private const val DURATION: Long = 3000 // 3초 대기 시간
        private const val PREFS_NAME = "app_prefs" // SharedPreferences 파일 이름
        private const val KEY_FIRST_RUN = "first_run" // 첫 실행 여부를 나타내는 키
    }
}