package com.example.myapplication.Login

import android.content.Context
import android.content.SharedPreferences
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.myapplication.Data.Request.BabyRequest
import com.example.myapplication.Data.Request.FamilyRoleRequest
import com.example.myapplication.Data.Request.bookmarkSetRequest
import com.example.myapplication.Data.Response.CommonResponse
import com.example.myapplication.Data.Response.DeleteDiaryResponse
import com.example.myapplication.R
import com.example.myapplication.Retrofit.AuthUtils
import com.example.myapplication.Retrofit.DiaryIF
import com.example.myapplication.Retrofit.LoginIF
import com.example.myapplication.Retrofit.RetrofitService
import com.example.myapplication.databinding.FragmentFamilyRoleBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class LoginFamilyRoleFragment : Fragment() {

    private var _binding: FragmentFamilyRoleBinding? = null
    private val binding get() = _binding!!
    private lateinit var apiService: LoginIF

    private lateinit var sharedPreferences: SharedPreferences

    private lateinit var adapter: RoleAdapter
    private var selectedRole: String = "역할을 선택해주세요"

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        _binding = FragmentFamilyRoleBinding.inflate(inflater, container, false)
        val view = binding.root

        val roles = mutableListOf("직접 작성할게요", "엄마", "아빠", "할머니", "할아버지")

        sharedPreferences = requireContext().getSharedPreferences("role", Context.MODE_PRIVATE)

        adapter = RoleAdapter(requireContext(), roles) { role ->
            selectedRole = role

            binding.tvSelectedRole.text = role
            binding.tvSelectedRole.setTextColor(Color.BLACK)
            binding.recyclerViewRoles.visibility = View.GONE

            // 역할 선택 후 SharedPreferences에 저장
            saveRoleToPreferences(role)

            // Enable the next button only if a valid role is selected
            binding.btnNext.isEnabled = selectedRole != "역할을 선택해주세요"
            binding.btnNext.setTextColor(ContextCompat.getColor(requireContext(), R.color.orange))
        }

        binding.recyclerViewRoles.layoutManager = LinearLayoutManager(context)
        binding.recyclerViewRoles.adapter = adapter

        binding.spinnerContainer.setOnClickListener {
            if (binding.recyclerViewRoles.visibility == View.VISIBLE) {
                binding.recyclerViewRoles.visibility = View.GONE
            } else {
                binding.recyclerViewRoles.visibility = View.VISIBLE
            }
        }

        // Initially, disable the next button
        binding.btnNext.isEnabled = false

        binding.btnBack.setOnClickListener {
            parentFragmentManager.popBackStack()
        }

        // 번들에서 아기 이름과 출산 예정일을 받아온 경우
        val babyName = arguments?.getString("baby_name")
        val dueDate = arguments?.getString("due_date")

        if (babyName != null && dueDate != null) {
            // 번들로 데이터가 전달된 경우
            binding.btnNext.setOnClickListener {
                val familyRole = binding.tvSelectedRole.text.toString()
                if (selectedRole != "역할을 선택해주세요") {
                    createNewBaby(babyName, dueDate, familyRole)
                }
            }

        } else {
            // 번들 없이 직접 전환된 경우
            binding.btnNext.setOnClickListener {
                val familyRole = binding.tvSelectedRole.text.toString()
                if (selectedRole != "역할을 선택해주세요") {
                    participateFamily(familyRole)
                }
            }
        }

        return view
    }

    private fun saveRoleToPreferences(role: String) {
        val editor = sharedPreferences.edit()
        editor.putString("selected_role", role)
        editor.apply()
    }

    private fun participateFamily(familyRole: String) {
        val token = AuthUtils.getAuthToken(requireContext())
        if (token == null) {
            Toast.makeText(context, "로그인 토큰이 없습니다. 다시 로그인해주세요.", Toast.LENGTH_SHORT).show()
            return
        }

        apiService = RetrofitService.createRetrofit(requireContext()).create(LoginIF::class.java)
        val request = FamilyRoleRequest(familyRole)
        apiService.participateFamily(request).enqueue(object : Callback<CommonResponse> {
            override fun onResponse(call: Call<CommonResponse>, response: Response<CommonResponse>) {
                if (response.isSuccessful) {
                    val result = response.body()
                    if (result != null && result.isSuccess) {
                        Log.d("가족 참여", "가족 참여삭제 성공")

                        val fragment = LoginCompleteFragment()
                        parentFragmentManager.beginTransaction()
                            .replace(R.id.main_frm2, fragment)
                            .addToBackStack(null)
                            .commit()

                    } else {
                        Log.e("가족 참여", "가족 참여 실패: ${response.code()}")
                        parentFragmentManager.beginTransaction()
                            .replace(R.id.main_frm2, LoginCode1Fragment())
                            .addToBackStack(null)
                            .commit()
                    }
                } else {
                    Log.e("가족 참여", "가족 참여 실패: ${response.code()}")
                }
            }

            override fun onFailure(call: Call<CommonResponse>, t: Throwable) {
                Log.e("가족 참여", "서버 통신 실패", t)
            }
        })
    }


    private fun createNewBaby(babyName: String, dueDate: String, familyRole: String) {
        val trimmedDueDate = dueDate.trim()
        // 날짜 형식이 'yyyy-MM-dd'인지 확인하고 한 자리 수일 경우 두 자리 수로 변환
        val formattedDueDate = formatDate(trimmedDueDate)
        val request = BabyRequest(babyName, formattedDueDate, familyRole)

        apiService = RetrofitService.createRetrofit(requireContext()).create(LoginIF::class.java)
        apiService.createNewBaby(request).enqueue(object : Callback<CommonResponse> {
            override fun onResponse(call: Call<CommonResponse>, response: Response<CommonResponse>) {
                if (response.isSuccessful) {
                    val result = response.body()
                    if (result != null && result.isSuccess) {
                        Log.d("아기 생성", "아기 생성 성공")

                        val fragment = LoginCompleteFragment()
                        parentFragmentManager.beginTransaction()
                            .replace(R.id.main_frm2, fragment)
                            .addToBackStack(null)
                            .commit()

                    } else {
                        Log.e("아기 생성", "아기 생성 실패: ${response.code()}")
                    }
                } else {
                    Log.e("아기 생성", "아기 생성 실패: ${response.code()}")
                }
            }

            override fun onFailure(call: Call<CommonResponse>, t: Throwable) {
                Log.e("아기 생성", "서버 통신 실패", t)
            }
        })
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun formatDate(date: String): String {
        val dateParts = date.split("-")
        if (dateParts.size == 3) {
            val year = dateParts[0]
            val month = dateParts[1].padStart(2, '0')  // 월을 두 자리 수로 변환
            val day = dateParts[2].padStart(2, '0')    // 일을 두 자리 수로 변환
            return "$year-$month-$day"
        }
        return date
    }
}
