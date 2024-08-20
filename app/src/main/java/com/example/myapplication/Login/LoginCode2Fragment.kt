package com.example.myapplication.Login

import android.graphics.Color
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat
import com.example.myapplication.Data.Request.BabyCodeRequest
import com.example.myapplication.Data.Request.bookmarkSetRequest
import com.example.myapplication.Data.Response.CommonResponse
import com.example.myapplication.R
import com.example.myapplication.Retrofit.AuthUtils
import com.example.myapplication.Retrofit.LoginIF
import com.example.myapplication.Retrofit.RetrofitService
import com.example.myapplication.databinding.FragmentLoginCode2Binding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class LoginCode2Fragment : Fragment() {

    lateinit var binding: FragmentLoginCode2Binding
    private lateinit var loginService: LoginIF

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentLoginCode2Binding.inflate(inflater, container, false)

        loginService = RetrofitService.createRetrofit(requireContext()).create(LoginIF::class.java)

        binding.ivCodeClear.setOnClickListener {
            binding.etCodeInput.text.clear()
        }

        // 초기에 다음 버튼을 비활성화
        binding.btnNext.isEnabled = false
        binding.etCodeInput.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                val isInputNotEmpty = s?.isNotEmpty() == true
                binding.btnNext.isEnabled = isInputNotEmpty

                // 버튼 색상 변경
                if (isInputNotEmpty) {
                    binding.btnNext.setTextColor(ContextCompat.getColor(requireContext(), R.color.orange))
                } else {
                    // 비활성화 상태의 색상 (예: 회색)i
                    binding.btnNext.setTextColor(Color.parseColor("#A1A1A1"))
                }
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })

        binding.btnBack.setOnClickListener {
            parentFragmentManager.popBackStack()
        }

        binding.btnNext.setOnClickListener {
            val babyCode = binding.etCodeInput.text.toString()
            postBabyCode(babyCode)
        }

        binding.ivLoginStart.setOnClickListener {
            val babyCode = binding.etCodeInput.text.toString()
            postBabyCode(babyCode)
        }

        return binding.root
    }

    private fun postBabyCode(babycode: String) {

        val request = BabyCodeRequest(babyCode = babycode)
        loginService.postBabyCode(request).enqueue(object : Callback<CommonResponse> {
            override fun onResponse(call: Call<CommonResponse>, response: Response<CommonResponse>) {
                if (response.isSuccessful) {
                    val commonResponse = response.body()
                    if (commonResponse?.isSuccess == true) {
                        // 성공 처리
                        Toast.makeText(context, commonResponse.message, Toast.LENGTH_SHORT).show()
                        // 다음 화면으로 이동
                        parentFragmentManager.beginTransaction()
                            .replace(R.id.main_frm2, LoginFamilyRoleFragment())
                            .addToBackStack(null)
                            .commit()
                    } else {
                        // 실패 처리
                        Toast.makeText(context, commonResponse?.message, Toast.LENGTH_SHORT).show()
                    }
                } else {
                    // HTTP 에러 처리
                    Toast.makeText(context, "서버 오류가 발생했습니다.", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<CommonResponse>, t: Throwable) {
                // 네트워크 오류 처리
                Toast.makeText(context, "네트워크 오류가 발생했습니다.", Toast.LENGTH_SHORT).show()
            }
        })
    }


}