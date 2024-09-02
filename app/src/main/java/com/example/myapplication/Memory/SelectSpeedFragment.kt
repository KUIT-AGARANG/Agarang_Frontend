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
import com.example.myapplication.Data.Request.MusicChoice
import com.example.myapplication.Data.Request.selectMusicRequest
import com.example.myapplication.Data.Response.SelectMusicResponse
import com.example.myapplication.MainActivity
import com.example.myapplication.R
import com.example.myapplication.Retrofit.MemoryIF
import com.example.myapplication.Retrofit.RetrofitService
import com.example.myapplication.Data.SharedViewModel
import com.example.myapplication.databinding.FragmentSelectSpeedBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class SelectSpeedFragment : Fragment() {

    private lateinit var binding: FragmentSelectSpeedBinding
    private var selectedSpeed: FrameLayout? = null
    private val sharedViewModel: SharedViewModel by activityViewModels()
    private var questionId: String? = null
    private lateinit var tempo: String

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
        when (selectedFrame.id) {
            R.id.fl_speed_fast -> {
                tempo = "FAST"
                applySelection(binding.flSpeedFast, binding.backgroundSelected, binding.genreOption)

            }
            R.id.fl_speed_medium -> {
                tempo = "MID"
                applySelection(binding.flSpeedMedium, binding.backgroundSelected2, binding.genreOption2)

            }
            R.id.fl_speed_slow -> {
                tempo = "SLOW"
                applySelection(binding.flSpeedSlow, binding.backgroundSelected3, binding.genreOption3)
            }
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

        sendToServer()
        Toast.makeText(requireContext(), "노래가 생성중이에요", Toast.LENGTH_LONG).show()


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
    private fun sendToServer() {
        val instrument=sharedViewModel.instrument.value
        val genre=sharedViewModel.genre.value //null
        val mood= sharedViewModel.mood.value

        Log.d("sendToServer", instrument.toString())
        Log.d("sendToServer", genre.toString())
        Log.d("sendToServer", mood.toString())
        Log.d("sendToServer", tempo)


        if (instrument!=null && genre !=null && mood!=null && tempo!=null){ //여기 Null 문제 때문에 오류
            val apiService= RetrofitService.createRetrofit(requireContext()).create(MemoryIF::class.java)
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

            apiService.sendSelectMusic(request).enqueue(object : Callback<SelectMusicResponse> {
                override fun onResponse(
                    call: Call<SelectMusicResponse>,
                    response: Response<SelectMusicResponse>
                ) {
                    if (response.isSuccessful) {
                        // 성공 처리
                        Log.d("FinFragment", "Data sent successfully: ${response.body()}")
                        val intent = Intent(requireContext(), MainActivity::class.java)
                        intent.putExtra("id", questionId)
                        startActivity(intent)
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