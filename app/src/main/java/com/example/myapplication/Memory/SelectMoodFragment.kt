package com.example.myapplication.Memory

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.GridView
import android.widget.ImageView
import androidx.fragment.app.activityViewModels
import com.example.myapplication.R
import com.example.myapplication.Data.SharedViewModel

class SelectMoodFragment : Fragment() {

    private val sharedViewModel: SharedViewModel by activityViewModels()
    private var questionId: String? = null

    @SuppressLint("MissingInflatedId")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_select_mood, container, false)

        val gridView = view.findViewById<GridView>(R.id.gv_mood)
        val moods = arrayOf(
            "아름다운", "밝은" , "행복한" , "평화로운", "따뜻한", "활기찬" , "기쁜" , "환상적인", "사랑스러운")
        // 번들로 전달된 데이터 가져오기
        arguments?.let { bundle ->
            questionId = bundle.getString("id")
            Log.d("deepquestion-bundle",questionId.toString())
        }
        val adapter = MoodAdapter(requireContext(), moods, requireActivity().supportFragmentManager, sharedViewModel,questionId.toString())

        gridView.adapter = adapter

        val imageView = view.findViewById<ImageView>(R.id.iv_baby_character)

        val sharedPreferences = requireContext().getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
        val selectedChar = sharedPreferences.getInt("selected_char", -1)

        if (selectedChar != -1) {
            // selectedChar 값을 사용하여 작업 수행
            imageView.setImageResource(selectedChar)
        }

        return view
    }

}