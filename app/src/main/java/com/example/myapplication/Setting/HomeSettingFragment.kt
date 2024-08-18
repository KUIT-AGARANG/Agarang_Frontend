package com.example.myapplication.Setting

import android.content.Context
import android.content.res.Resources
import android.icu.text.SimpleDateFormat
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView

import com.example.myapplication.Data.Response.HomeSettingResponse
import com.example.myapplication.Data.Response.Result
import com.example.myapplication.R
import com.example.myapplication.Retrofit.HomeIF
import com.example.myapplication.Retrofit.RetrofitService

import com.example.myapplication.databinding.FragmentHomeSettingBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import java.util.Calendar
import java.util.Date
import java.util.Locale

class HomeSettingFragment : Fragment() {
    lateinit var binding: FragmentHomeSettingBinding
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding= FragmentHomeSettingBinding.inflate(inflater,container,false)

        fetchBabyInfo()

        binding.ivChildInfoPlus.setOnClickListener {
            val fragment=ChildInfoChangeFragment()
            val transaction=parentFragmentManager.beginTransaction()
            transaction.replace(R.id.main_frm,fragment)
            transaction.addToBackStack(null)
            transaction.commit()
        }

        binding.ivFamilyInfoPlus.setOnClickListener {
            val fragment=FamilyInfoFragment()
            val transaction=parentFragmentManager.beginTransaction()
            transaction.replace(R.id.main_frm,fragment)
            transaction.addToBackStack(null)
            transaction.commit()
        }

        binding.ivCharInfoPlus.setOnClickListener {
            val fragmentChangChar= ChangeCharFragment()
            val transaction=parentFragmentManager.beginTransaction()
            transaction.replace(R.id.main_frm,fragmentChangChar)
                .commit()
        }

        val sharedPreferences = requireContext().getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
        val selectedChar = sharedPreferences.getInt("selected_char", -1)

        if (selectedChar != -1) {
            // selectedChar 값을 사용하여 작업 수행
            binding.sivProperty.setImageResource(selectedChar)
        } else {
            Log.e("HomeSettingFragment", "Invalid resource ID: $selectedChar")
            // 기본 이미지 설정
            binding.sivProperty.setImageResource(R.drawable.forest)
        }



        fetchBabyInfo()
        return binding.root
    }

    private fun fetchBabyInfo() {
        val service = RetrofitService.createRetrofit(requireContext()).create(HomeIF::class.java)

        service.getSettingData().enqueue(object : Callback<HomeSettingResponse> {
            override fun onResponse(
                call: Call<HomeSettingResponse>,
                response: Response<HomeSettingResponse>
            ) {
                if (response.isSuccessful) {
                    val apiResponse = response.body()

                    if (apiResponse != null && apiResponse.isSuccess) {
                        updateUI(apiResponse.result)
                    } else {
                        // 에러 처리
                        Log.e("오류", "API 요청이 성공하지 못했습니다: ${apiResponse?.message}")
                    }
                } else {
                    // 오류 응답 처리
                    val errorBody = response.errorBody()?.string()
                    Log.e("오류", "Response error: $errorBody")
                }

            }

            override fun onFailure(call: Call<HomeSettingResponse>, t: Throwable) {
                // 네트워크 오류 또는 다른 문제 처리
                binding.tvProfileDday.text = "Error: ${t.message}"
            }
        })
    }

    private fun updateUI(result: HomeSettingResponse.Result){
        binding.tvProfileName.text = result.babyName
        binding.tvProfileDday.text = "D-${result.dday}"
        binding.tvProfileDueDate.text = result.dueDate
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
/*
        val selectedImageResourceId = arguments?.getInt("selected_char") ?: return
        val imageView: ImageView = view.findViewById(R.id.siv_property)
        imageView.setImageResource(selectedImageResourceId)*/

    }
}