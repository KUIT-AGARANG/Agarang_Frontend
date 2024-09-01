package com.example.myapplication.Setting

import android.icu.text.SimpleDateFormat
import android.os.Bundle
import android.text.Editable
import android.text.InputType
import android.text.TextWatcher
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.FragmentTransaction
import androidx.fragment.app.activityViewModels
import com.example.myapplication.Data.Request.ChildInfoUpdateRequest
import com.example.myapplication.Data.Response.HomeChildResponse
import com.example.myapplication.Data.Response.HomeChildUpdateResponse
import com.example.myapplication.R
import com.example.myapplication.Retrofit.HomeIF
import com.example.myapplication.Retrofit.RetrofitService
import com.example.myapplication.Data.SharedViewModel
import com.example.myapplication.databinding.FragmentChildInfoChangeBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.Date
import java.util.Locale

class ChildInfoChangeFragment : Fragment(), CalendarFragment.OnDateSelectedListener {
    lateinit var binding: FragmentChildInfoChangeBinding
    private val sharedViewModel: SharedViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentChildInfoChangeBinding.inflate(inflater, container, false)

        fetchBabyInfoFromServer()

        binding.btnNameCancle.setOnClickListener {
            binding.etBirthName.setText("")
        }

        binding.icCancleBtn.setOnClickListener {
            val currentText = binding.etWeight.text.toString()
            val newText = currentText.replace(Regex("\\d+(\\.\\d+)?\\s*"), "")
            binding.etWeight.setText(newText)
        }

        binding.ivIcCalendar.setOnClickListener {
            val calendarFragment = CalendarFragment()
            calendarFragment.setTargetFragment(this, 0)
            calendarFragment.show(parentFragmentManager, "datePicker")
        }

        binding.ivCancleBox.setOnClickListener {
            parentFragmentManager.popBackStack()
        }

        binding.ivOkBox.setOnClickListener {
            val babyName = binding.etBirthName.text.toString().takeIf { it.isNotEmpty() } ?: binding.etBirthName.hint.toString()
            val dueDate = binding.tvBirthDate.text.toString()
            val weight = binding.etWeight.text.toString().replace(" kg", "").toFloatOrNull() ?: 0f

            sendChildUpdateRequest(babyName, dueDate, weight)
        }
        binding.etWeight.apply {
            // 숫자만 입력 가능하도록 설정
            inputType = InputType.TYPE_CLASS_NUMBER or InputType.TYPE_NUMBER_FLAG_DECIMAL

            addTextChangedListener(object : TextWatcher {
                private var isUpdating = false

                override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                    if (isUpdating) return

                    if (!s.isNullOrEmpty() && !s.toString().endsWith("kg")) {
                        isUpdating = true

                        val textWithoutKg = s.toString().replace("kg", "").trim()
                        val updatedText = "$textWithoutKg kg"

                        setText(updatedText)
                        setSelection(textWithoutKg.length) // 커서를 "kg" 앞에 위치
                        isUpdating = false
                    }
                }

                override fun afterTextChanged(s: Editable?) {}
            })
        }
        //이건 왜 없앤거지
        /*binding.etWeight.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            *//*override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if (!s.isNullOrEmpty() && !s.toString().endsWith("kg")) {
                    binding.etWeight.setText("$s kg")
                    binding.etWeight.setSelection(s.length)
                }
            }*//*
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if (!s.isNullOrEmpty() && !s.toString().endsWith("kg")) {
                    val updatedText = "$s kg"
                    binding.etWeight.setText(updatedText)
                    binding.etWeight.setSelection(updatedText.length - 3) // 커서를 kg 앞에 위치
                }
            }

            override fun afterTextChanged(s: Editable?) {}
        })*/

        return binding.root
    }

    private fun fetchBabyInfoFromServer() {
        val service = RetrofitService.createRetrofit(requireContext()).create(HomeIF::class.java)

        service.getChildInfoData().enqueue(object : Callback<HomeChildResponse> {
            override fun onResponse(
                call: Call<HomeChildResponse>,
                response: Response<HomeChildResponse>
            ) {
                if (response.isSuccessful) {
                    val apiResponse = response.body()

                    if (apiResponse != null && apiResponse.isSuccess) {
                        updateUI(apiResponse.result)
                    } else {
                        // 에러 처리
                        Log.e("오류", "API 요청이 성공하지 못했습니다: ${apiResponse?.message}")
                        Toast.makeText(context, "서버 오류: ${apiResponse?.message}", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    // 오류 응답 처리
                    val errorBody = response.errorBody()?.string()
                    Log.e("오류", "Response error: $errorBody")
                    Toast.makeText(context, "응답 오류: $errorBody", Toast.LENGTH_SHORT).show()
                }

            }

            override fun onFailure(call: Call<HomeChildResponse>, t: Throwable) {
                Log.e("오류", "네트워크 오류: ${t.message}")
                Toast.makeText(context, "네트워크 오류: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun updateUI(babyInfo: HomeChildResponse.Result) {
        // 서버에서 받아온 데이터를 EditText의 hint로 설정
        binding.etBirthName.hint = babyInfo.babyName ?: "태명을 입력하세요."
        binding.tvBirthDate.text = babyInfo.dueDate ?: ""
        binding.etWeight.hint = "${babyInfo.weight} kg"
    }

    override fun onDateSelected(date: String) {
        // 날짜를 yyyy-MM-dd 형식으로 변환
        val inputFormat = SimpleDateFormat("yyyy-M-d", Locale.getDefault())
        val outputFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val parsedDate: Date? = inputFormat.parse(date)
        val formattedDate = parsedDate?.let { outputFormat.format(it) } ?: date
        binding.tvBirthDate.text = formattedDate
    }


    //수정 API 연동
    private fun sendChildUpdateRequest(babyName: String, dueDate: String, weight: Float) {
        val service = RetrofitService.createRetrofit(requireContext()).create(HomeIF::class.java)

        // PATCH 요청의 body 데이터
        val requestBody = ChildInfoUpdateRequest(babyName,dueDate,weight)

        service.updateChildInfo(requestBody).enqueue(object : Callback<HomeChildUpdateResponse> {
            override fun onResponse(call: Call<HomeChildUpdateResponse>, response: Response<HomeChildUpdateResponse>) {
                if (response.isSuccessful) {
                    val apiResponse = response.body()

                    if (apiResponse != null && apiResponse.isSuccess) {
                        Toast.makeText(requireContext(), "아이정보가 성공적으로 변경되었습니다.", Toast.LENGTH_SHORT).show()
                        navigateToHomeSettingFragment() // 성공 시 HomeSettingFragment로 전환
                    } else {
                        Toast.makeText(requireContext(), "요청에 실패했습니다: ${apiResponse?.message}", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    Toast.makeText(requireContext(), "응답 오류: ${response.errorBody()?.string()}", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<HomeChildUpdateResponse>, t: Throwable) {
                Toast.makeText(requireContext(), "API 요청 실패: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }
    private fun navigateToHomeSettingFragment() {
        val fragmentTransaction = requireActivity().supportFragmentManager.beginTransaction()
        fragmentTransaction.replace(R.id.main_frm, HomeSettingFragment())
        fragmentTransaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
        fragmentTransaction.addToBackStack(null)
        fragmentTransaction.commit()
    }
}
