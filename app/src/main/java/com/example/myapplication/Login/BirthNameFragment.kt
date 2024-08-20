package com.example.myapplication.Login

import android.graphics.Color
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import com.example.myapplication.R
import com.example.myapplication.databinding.FragmentBirthNameBinding


class BirthNameFragment : Fragment() {
    lateinit var binding:FragmentBirthNameBinding
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentBirthNameBinding.inflate(inflater,container,false)

        binding.ivCodeClear.setOnClickListener {
            binding.etBirthName.text.clear()
        }

        // 초기에 다음 버튼을 비활성화
        binding.btnNext.isEnabled = false
        binding.etBirthName.addTextChangedListener(object : TextWatcher {
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

        binding.btnNext.setOnClickListener {
            val babyName = binding.etBirthName.text.toString()

            val fragment = DueDateFragment()
            val bundle = Bundle().apply {
                putString("baby_name", babyName)
            }
            fragment.arguments = bundle

            parentFragmentManager.beginTransaction()
                .replace(R.id.main_frm2, fragment) // Replace with the correct container ID
                .addToBackStack(null)
                .commit()
        }

        binding.btnBack.setOnClickListener {
            parentFragmentManager.popBackStack()
        }

        return binding.root
    }



}