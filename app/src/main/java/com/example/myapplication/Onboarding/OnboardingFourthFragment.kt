package com.example.myapplication.Onboarding

import android.graphics.Color
import android.os.Bundle
import android.text.Spannable
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.example.myapplication.databinding.FragmentOnboardingFourthBinding


class OnboardingFourthFragment : Fragment() {

    lateinit var binding: FragmentOnboardingFourthBinding


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentOnboardingFourthBinding.inflate(inflater, container, false)
        // TextView를 찾아옵니다.
        val textView: TextView = binding.tvOnboardingMent
        // 전체 텍스트 설정
        val fullText = "취향 선택을 통해 "

        // SpannableString 생성
        val spannableString = SpannableString(fullText)

        // "취향 선택" 텍스트의 시작과 끝 인덱스를 찾아 색상 적용
        val startIndex = fullText.indexOf("취향 선택")
        val endIndex = startIndex + "취향 선택".length
        val color = Color.parseColor("#EB5F2A")

        // ForegroundColorSpan을 사용하여 색상 적용
        spannableString.setSpan(
            ForegroundColorSpan(color),
            startIndex,
            endIndex,
            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
        )
        // TextView에 SpannableString 설정
        textView.text = spannableString

        return binding.root
    }


}