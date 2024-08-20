package com.example.myapplication.Login

import android.graphics.Color
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.text.InputType
import androidx.core.content.ContextCompat
import com.example.myapplication.R
import com.example.myapplication.Setting.CalendarFragment
import com.example.myapplication.databinding.FragmentDueDateBinding

class DueDateFragment : Fragment(), CalendarFragment.OnDateSelectedListener {
    lateinit var binding: FragmentDueDateBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentDueDateBinding.inflate(inflater, container, false)

        // EditText를 클릭해도 키보드가 나오지 않도록 설정
        binding.etDueDate.inputType = InputType.TYPE_NULL

        // EditText 클릭 시 DatePicker 표시
        binding.etDueDate.setOnClickListener {
            showDatePicker()
        }

        binding.ivIcCalendar.setOnClickListener {
            showDatePicker()
        }

        // 초기에 다음 버튼을 비활성화
        binding.btnNext.isEnabled = false
        updateNextButtonState()

        val babyName = arguments?.getString("baby_name")

        binding.btnBack.setOnClickListener {
            parentFragmentManager.popBackStack()
        }

        binding.btnNext.setOnClickListener {
            // Get the due date from the EditText
            val dueDate = binding.etDueDate.text.toString()

            // Create a Bundle to pass the data to the next fragment
            val bundle = Bundle().apply {
                putString("baby_name", babyName)
                putString("due_date", dueDate)
            }

            // Create the next fragment and pass the bundle
            val nextFragment = LoginFamilyRoleFragment() // Replace with the actual next fragment
            nextFragment.arguments = bundle

            parentFragmentManager.beginTransaction()
                .replace(R.id.main_frm2, nextFragment) // Replace with the correct container ID
                .addToBackStack(null)
                .commit()
        }

        return binding.root
    }

    private fun showDatePicker() {
        val calendarFragment = CalendarFragment()
        calendarFragment.setTargetFragment(this, 0)
        calendarFragment.show(parentFragmentManager, "datePicker")
    }

    private fun updateNextButtonState() {
        val isDateEntered = binding.etDueDate.text.toString().trim().isNotEmpty()
        binding.btnNext.isEnabled = isDateEntered
        // 버튼 색상 변경
        if (isDateEntered) {
            binding.btnNext.setTextColor(ContextCompat.getColor(requireContext(), R.color.orange))
        } else {
            // 비활성화 상태의 색상 (예: 회색)
            binding.btnNext.setTextColor(Color.parseColor("#A1A1A1"))
        }
    }

    override fun onDateSelected(date: String) {
        binding.etDueDate.setText("  " + date)
        binding.etDueDate.setTextColor(Color.BLACK)
        updateNextButtonState()
    }
}