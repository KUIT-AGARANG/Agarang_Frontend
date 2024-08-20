package com.example.myapplication.Memory

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.activityViewModels
import com.example.myapplication.MainActivity
import com.example.myapplication.R
import com.example.myapplication.SharedViewModel
import com.example.myapplication.databinding.FragmentSelectSpeedBinding

class SelectSpeedFragment : Fragment() {

    private lateinit var binding: FragmentSelectSpeedBinding
    private var selectedSpeed: FrameLayout? = null
    private val sharedViewModel: SharedViewModel by activityViewModels()
    private var questionId: String? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = FragmentSelectSpeedBinding.inflate(inflater, container, false)
        val sharedPreferences = requireContext().getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
        val selectedChar = sharedPreferences.getInt("selected_char", -1)

        if (selectedChar != -1) {
            // selectedChar 값을 사용하여 작업 수행
            binding.ivBabyCharacter.setImageResource(selectedChar)
        }
        // 번들로 전달된 데이터 가져오기
        arguments?.let { bundle ->
            questionId = bundle.getString("id")
            Log.d("deepquestion-bundle",questionId.toString())
        }
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupSpeedSelection()
    }

    private fun setupSpeedSelection() {
        val speedOptions = listOf(binding.flSpeedFast, binding.flSpeedMedium, binding.flSpeedSlow)

        speedOptions.forEach { frameLayout ->
            frameLayout.setOnClickListener {
                updateSelection(frameLayout)
            }
        }
    }

    private fun updateSelection(selectedFrame: FrameLayout) {
        // 이전 선택 초기화
        selectedSpeed?.let { resetSelection(it) }

        // 새로운 선택 적용
        val tempo = when (selectedFrame.id) {
            R.id.fl_speed_fast -> {
                applySelection(binding.flSpeedFast, binding.backgroundSelected, binding.genreOption)
                "FAST"
            }
            R.id.fl_speed_medium -> {
                applySelection(binding.flSpeedMedium, binding.backgroundSelected2, binding.genreOption2)
                "MID"
            }
            R.id.fl_speed_slow -> {
                applySelection(binding.flSpeedSlow, binding.backgroundSelected3, binding.genreOption3)
                "SLOW"
            }
            else -> null
        }

        selectedSpeed = selectedFrame
        tempo?.let {
            Log.e("SelectSpeedFragment",it)
            sharedViewModel.setTempo(it)
        }
    }

    private fun applySelection(frameLayout: FrameLayout, backgroundSelected: ImageView, textView: TextView) {
        backgroundSelected.visibility = View.VISIBLE
        textView.setTextColor(Color.parseColor("#EB5F2A"))
        // 프래그먼트 전환
        /*val fragment = FinFragment()
        val bundle = Bundle()
        bundle.putString("id", questionId)
        fragment.arguments = bundle
        parentFragmentManager.beginTransaction()
            .replace(R.id.memory_frm, fragment)
            .addToBackStack(null)
            .commit()*/

    // 홈으로 전환되는 걸로 수정!
        Toast.makeText(requireContext(), "노래가 생성중이에요", Toast.LENGTH_LONG).show()
        val intent = Intent(requireContext(), MainActivity::class.java)
        intent.putExtra("id", questionId)
        startActivity(intent)

    }

    private fun resetSelection(frameLayout: FrameLayout) {
        when (frameLayout.id) {
            R.id.fl_speed_fast -> {
                binding.backgroundSelected.visibility = View.GONE
                binding.genreOption.setTextColor(Color.parseColor("#787878"))
            }
            R.id.fl_speed_medium -> {
                binding.backgroundSelected2.visibility = View.GONE
                binding.genreOption2.setTextColor(Color.parseColor("#787878"))
            }
            R.id.fl_speed_slow -> {
                binding.backgroundSelected3.visibility = View.GONE
                binding.genreOption3.setTextColor(Color.parseColor("#787878"))
            }
        }
    }
}