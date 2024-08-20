package com.example.myapplication.Login

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.myapplication.MainActivity
import com.example.myapplication.R
import com.example.myapplication.databinding.FragmentLoginCode1Binding


class LoginCode1Fragment : Fragment() {

    lateinit var binding: FragmentLoginCode1Binding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentLoginCode1Binding.inflate(inflater, container, false)

        // 저장된 역할이 있는지 확인
        val sharedPreferences = requireContext().getSharedPreferences("role", Context.MODE_PRIVATE)
        val savedRole = sharedPreferences.getString("selected_role", null)

        if (savedRole != null) {
            // 이미 역할이 선택된 경우 바로 MainActivity로 이동
            Log.d("로그인 코드 1", "메인 이동")
            val intent = Intent(activity, MainActivity::class.java)
            startActivity(intent)
            activity?.finish() // 현재 액티비티 종료
        }

        binding.ivInputCode.setOnClickListener {
            parentFragmentManager.beginTransaction()
                .replace(R.id.main_frm2, LoginCode2Fragment())
                .addToBackStack(null)
                .commit()
        }

        binding.ivRegisterCode.setOnClickListener {
            parentFragmentManager.beginTransaction()
                .replace(R.id.main_frm2, BirthNameFragment())
                .addToBackStack(null)
                .commit()
        }


        return binding.root
    }


}