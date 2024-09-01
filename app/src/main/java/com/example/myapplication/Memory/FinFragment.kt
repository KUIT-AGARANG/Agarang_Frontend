package com.example.myapplication.Memory

import android.content.Context
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
import androidx.fragment.app.activityViewModels
import com.example.myapplication.Data.Request.MusicChoice
import com.example.myapplication.Data.Request.selectMusicRequest
import com.example.myapplication.Data.Response.SelectMusicResponse
import com.example.myapplication.R
import com.example.myapplication.Retrofit.MemoryIF
import com.example.myapplication.Retrofit.RetrofitService
import com.example.myapplication.Data.SharedViewModel
import com.example.myapplication.databinding.FragmentFinBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class FinFragment : Fragment() {

    private lateinit var binding: FragmentFinBinding
    private var selectedOption: FrameLayout? = null
    private val sharedViewModel: SharedViewModel by activityViewModels()
    private var questionId: String? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = FragmentFinBinding.inflate(inflater, container, false)

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

        setupOptionSelection()
    }

    private fun setupOptionSelection() {
        val options = listOf(binding.flFinYes, binding.flFinNo)

        options.forEach { frameLayout ->
            frameLayout.setOnClickListener {
                updateSelection(frameLayout)
            }
        }
    }

    private fun updateSelection(selectedFrame: FrameLayout) {
        // 이전 선택 초기화
        selectedOption?.let { resetSelection(it) }

        // 새로운 선택 적용
        when (selectedFrame.id) {
            R.id.fl_fin_yes -> {
                applySelection(binding.flFinYes, binding.backgroundSelected, binding.genreOption)
                sendToServer()
            }
            R.id.fl_fin_no -> applySelection(binding.flFinNo, binding.backgroundSelected2, binding.genreOption2)
        }

        selectedOption = selectedFrame
    }

    private fun applySelection(frameLayout: FrameLayout, backgroundSelected: ImageView, textView: TextView) {
        backgroundSelected.visibility = View.VISIBLE
        textView.setTextColor(Color.parseColor("#EB5F2A"))
    }

    private fun resetSelection(frameLayout: FrameLayout) {
        when (frameLayout.id) {
            R.id.fl_fin_yes -> {
                binding.backgroundSelected.visibility = View.GONE
                binding.genreOption.setTextColor(Color.parseColor("#787878"))
            }
            R.id.fl_fin_no -> {
                binding.backgroundSelected2.visibility = View.GONE
                binding.genreOption2.setTextColor(Color.parseColor("#787878"))
            }
        }
    }

    private fun sendToServer() {
        val instrument=sharedViewModel.instrument.value
        val genre=sharedViewModel.genre.value //null
        val mood= sharedViewModel.mood.value
        val tempo=sharedViewModel.tempo.value

        Log.d("sendToServer", instrument.toString())
        Log.d("sendToServer", genre.toString())
        Log.d("sendToServer", mood.toString())
        Log.d("sendToServer", tempo.toString())


        if (instrument!=null && genre !=null && mood!=null && tempo!=null){ //여기 Null 문제 때문에 오류
            val apiService=RetrofitService.createRetrofit(requireContext()).create(MemoryIF::class.java)
            val music= MusicChoice(
                instrument=instrument,
                genre=genre,
                mood=mood,
                tempo=tempo
            )
            val request= selectMusicRequest(
                id=questionId.toString(),
                musicChoice = music
            )
            apiService.sendSelectMusic(request).enqueue(object : Callback<SelectMusicResponse>{
                override fun onResponse(
                    call: Call<SelectMusicResponse>,
                    response: Response<SelectMusicResponse>
                ) {
                    if (response.isSuccessful) {
                        // 성공 처리
                        Log.d("FinFragment", "Data sent successfully: ${response.body()}")
                    } else {
                        // 오류 처리
                        Log.e("FinFragment", "Error sending data: ${response.errorBody()}")
                    }
                }

                override fun onFailure(call: Call<SelectMusicResponse>, t: Throwable) {
                    Log.e("FinFragment", "Failed to send data", t)
                }

            })
        }else{
            Log.e("FinFragment","Failed to send data")
        }
    }

}