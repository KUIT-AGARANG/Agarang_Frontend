package com.example.myapplication.Memory

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.media.MediaPlayer
import android.media.MediaRecorder
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowInsetsAnimation
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.myapplication.Data.Request.Memory2Request
import com.example.myapplication.Data.Response.Memory2Response
import com.example.myapplication.R
import com.example.myapplication.Retrofit.DiaryIF
import com.example.myapplication.Retrofit.MemoryIF
import com.example.myapplication.Retrofit.RetrofitService
import com.example.myapplication.databinding.FragmentDeepQuestionBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.File
import java.io.IOException

class DeepQuestionFragment : Fragment() {
    private lateinit var binding:FragmentDeepQuestionBinding
    // 변수 선언
    private var questionId: String? = null
    private var questionText: String? = null
    private var audioUrl: String? = null

    private var mediaRecorder: MediaRecorder? = null
    private var audioFile: File? = null
    private var mediaPlayer: MediaPlayer? = null
//clova key
    private val clientId = "nlpxphm34l"
    private val clientSecret = "B4F7SeFMWV7UpjzOcuu6Kb0nsEuz8EUaF6HYOL44"

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding= FragmentDeepQuestionBinding.inflate(inflater,container,false)

        // 번들로 전달된 데이터 가져오기
        arguments?.let { bundle ->
            questionId = bundle.getString("id")
            questionText = bundle.getString("text")
            audioUrl = bundle.getString("audioUrl")

            Log.d("deepquestion-bundle",questionText.toString())

            binding.tvQuestionTopic.text = questionText
        }



        Log.d("PicAssociationFragment", "Question ID: $questionId")
        Log.d("PicAssociationFragment", "Question Text: $questionText")
        Log.d("PicAssociationFragment", "Audio URL: $audioUrl")



        // 녹음 권한 요청
        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED ||
            ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(requireActivity(), arrayOf(Manifest.permission.RECORD_AUDIO, Manifest.permission.WRITE_EXTERNAL_STORAGE), 1)
        }

        if (audioUrl != null) {
            playAudio()
        }
        binding.ivRecordBtn.setOnClickListener {
            binding.ivRecordBtn.visibility=View.GONE
            binding.tvRecordNotice.visibility=View.VISIBLE
            binding.ivRecordCancleBtn.visibility=View.VISIBLE
            binding.ivRecordArrowBtn.visibility=View.VISIBLE
            binding.ivRecordIng.visibility=View.VISIBLE
            binding.ivRecordingNextBtn.visibility=View.GONE

        }
        binding.ivRecordingNextBtn.setOnClickListener {
            val fragment=SelectInstrumentFragment()
            val bundle = Bundle()
            bundle.putString("id", questionId)
            fragment.arguments = bundle
            parentFragmentManager.beginTransaction()
                .replace(R.id.memory_frm,fragment)
                .commit()
        }

        binding.ivRecordCancleBtn.setOnClickListener {
            binding.ivRecordBtn.visibility=View.VISIBLE
            binding.tvRecordNotice.visibility=View.GONE
            binding.ivRecordCancleBtn.visibility=View.GONE
            binding.ivRecordArrowBtn.visibility=View.GONE
            binding.ivRecordIng.visibility=View.GONE
            binding.ivRecordingNextBtn.visibility=View.GONE
        }

        binding.ivRecordArrowBtn.setOnClickListener {
            binding.ivRecordBtn.visibility=View.GONE
            binding.tvRecordNotice.visibility=View.GONE
            binding.ivRecordCancleBtn.visibility=View.GONE
            binding.ivRecordArrowBtn.visibility=View.GONE
            binding.ivRecordIng.visibility=View.GONE
            binding.ivRecordingNextBtn.visibility=View.VISIBLE


            //서버 요청메소드 호출
            sendMemoryDetailsToServer(questionId.toString(),questionText.toString())

        }

        val sharedPreferences = requireContext().getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
        val selectedChar = sharedPreferences.getInt("selected_char", -1)

        if (selectedChar != -1) {
            // selectedChar 값을 사용하여 작업 수행
            binding.ivBabyCharacter.setImageResource(selectedChar)
        }


        return binding.root
    }

    private fun playAudio() {
        if (audioUrl == null) {
            Log.e("DeepQuestionFragment", "Audio URL is null")
            return
        }

        mediaPlayer = MediaPlayer().apply {
            setOnPreparedListener {
                start()
            }
            try {
                setDataSource(audioUrl)
                prepareAsync()
            } catch (e: IOException) {
                Log.e("PicAssociationFragment", "Error playing audio", e)
            }
        }
    }


    private fun sendMemoryDetailsToServer(id: String, text: String) {
        val service = RetrofitService.createRetrofit(requireContext()).create(MemoryIF::class.java)
        val request = Memory2Request(id, text)

        service.sendMemoryDetails(request).enqueue(object : Callback<Memory2Response> {
            override fun onResponse(call: Call<Memory2Response>, response: Response<Memory2Response>) {
                if (response.isSuccessful) {
                    val apiResponse = response.body()
                    if (apiResponse != null && apiResponse.isSuccess) {
                        // 성공적인 응답 처리
                        Log.d("DiaryMainAnswerFragment", "성공: ${apiResponse.message}")
                        binding.tvRecordNotice.text = apiResponse.message
                    } else {
                        // 에러 처리
                        Log.e("DiaryMainAnswerFragment", "에러: ${response.errorBody()?.string()}")
                    }
                } else {
                    // 오류 응답 처리
                    Log.e("DiaryMainAnswerFragment", "응답 오류: ${response.errorBody()?.string()}")
                }
            }

            override fun onFailure(call: Call<Memory2Response>, t: Throwable) {
                Log.d("DiaryMainAnswerFragment", "실패: ${t.message}")
            }
        })
    }
}