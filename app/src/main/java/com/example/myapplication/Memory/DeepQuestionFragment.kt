package com.example.myapplication.Memory

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.media.MediaPlayer
import android.media.MediaRecorder
import android.os.Bundle
import android.os.Environment
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowInsetsAnimation
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.myapplication.Data.Request.FirstAnsRequest
import com.example.myapplication.Data.Request.Memory2Request
import com.example.myapplication.Data.Response.FirstAnsResponse
import com.example.myapplication.Data.Response.Memory2Response
import com.example.myapplication.R
import com.example.myapplication.Retrofit.DiaryIF
import com.example.myapplication.Retrofit.MemoryIF
import com.example.myapplication.Retrofit.RetrofitService
import com.example.myapplication.databinding.FragmentDeepQuestionBinding
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.asRequestBody
import org.json.JSONException
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.File
import java.io.IOException

class DeepQuestionFragment : Fragment() {
    private lateinit var binding:FragmentDeepQuestionBinding
    private var questionId: String? = null
    private var questionText: String? = null
    private var audioUrl: String? = null
    private var audioFile: File? = null
    private var mediaRecorder: MediaRecorder? = null
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
            Log.d("DeepQuestionFragment", "Record button clicked")
            startRecording()
            binding.ivRecordBtn.visibility=View.GONE
            binding.tvRecordNotice.visibility=View.VISIBLE
            binding.ivRecordCancleBtn.visibility=View.VISIBLE
            binding.ivRecordArrowBtn.visibility=View.VISIBLE
            binding.ivRecordIng.visibility=View.VISIBLE
            binding.ivRecordingNextBtn.visibility=View.GONE

        }
        /*binding.ivRecordingNextBtn.setOnClickListener {
            val fragment=SelectInstrumentFragment()
            val bundle = Bundle()
            bundle.putString("id", questionId)
            fragment.arguments = bundle
            parentFragmentManager.beginTransaction()
                .replace(R.id.memory_frm,fragment)
                .commit()
        }*/

        binding.ivRecordCancleBtn.setOnClickListener {
            Log.d("DeepQuestionFragment", "Cancel button clicked")
            stopRecording()

            binding.ivRecordBtn.visibility=View.VISIBLE
            binding.tvRecordNotice.visibility=View.GONE
            binding.ivRecordCancleBtn.visibility=View.GONE
            binding.ivRecordArrowBtn.visibility=View.GONE
            binding.ivRecordIng.visibility=View.GONE
            binding.ivRecordingNextBtn.visibility=View.GONE
        }

        binding.ivRecordArrowBtn.setOnClickListener {
            Log.d("DeepQuestionFragment", "Arrow button clicked")
            stopRecording()

            binding.ivRecordBtn.visibility=View.GONE
            binding.tvRecordNotice.visibility=View.GONE
            binding.ivRecordCancleBtn.visibility=View.GONE
            binding.ivRecordArrowBtn.visibility=View.GONE
            binding.ivRecordIng.visibility=View.GONE
            binding.ivRecordingNextBtn.visibility=View.VISIBLE


            //서버 요청메소드 호출
            /*sendMemoryDetailsToServer(questionId.toString(),questionText.toString())*/
            uploadAudioFileAndGetText()
        }

        val sharedPreferences = requireContext().getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
        val selectedChar = sharedPreferences.getInt("selected_char", -1)

        if (selectedChar != -1) {
            // selectedChar 값을 사용하여 작업 수행
            binding.ivBabyCharacter.setImageResource(selectedChar)
        }
        return binding.root
    }

    private fun startRecording() {
        Log.d("PicAssociationFragment", "Starting recording")
        audioFile = File(requireContext().getExternalFilesDir(Environment.DIRECTORY_MUSIC), "recorded_audio.m4a")
        mediaRecorder = MediaRecorder().apply {
            setAudioSource(MediaRecorder.AudioSource.MIC)
            setOutputFormat(MediaRecorder.OutputFormat.MPEG_4)
            setAudioEncoder(MediaRecorder.AudioEncoder.AAC)
            setAudioEncodingBitRate(128000)
            setAudioSamplingRate(44100)
            setOutputFile(audioFile?.absolutePath)
            try {
                prepare()
                start()
                Log.d("PicAssociationFragment", "Recording started")
            } catch (e: IOException) {
                Log.e("PicAssociationFragment", "Failed to start recording", e)
                /* e.printStackTrace()*/
            }
        }
    }
    private fun stopRecording() {
        Log.d("PicAssociationFragment", "Stopping recording")
        mediaRecorder?.apply {
            stop()
            release()
        }
        mediaRecorder = null
    }

    private fun uploadAudioFileAndGetText() {
        Log.d("UploadAudio", "Uploading audio file")
        val url = "https://naveropenapi.apigw.ntruss.com/recog/v1/stt?lang=Kor"
        val client = OkHttpClient()

        if (audioFile == null || !audioFile!!.exists()) {
            Log.e("UploadAudio", "Audio file does not exist")
            return
        }

        val mediaType = "application/octet-stream".toMediaTypeOrNull()
        val requestBody = audioFile!!.asRequestBody(mediaType)

        val request = Request.Builder()
            .url(url)
            .addHeader("X-NCP-APIGW-API-KEY-ID", clientId)
            .addHeader("X-NCP-APIGW-API-KEY", clientSecret)
            .addHeader("Content-Type", "application/octet-stream")
            .post(requestBody)
            .build()

        client.newCall(request).enqueue(object : okhttp3.Callback {
            override fun onFailure(call: okhttp3.Call, e: IOException) {
                Log.e("UploadAudio", "API call failed", e)
            }

            override fun onResponse(call: okhttp3.Call, response: okhttp3.Response) {
                val responseBody = response.body?.string()
                if (response.isSuccessful && responseBody != null) {
                    Log.d("성공", "API Response: ${responseBody}")
                    try {
                        val jsonObject = JSONObject(responseBody)
                        val text = jsonObject.optString("text")
                        Log.d("성공", "Extracted text: $text")
                        activity?.runOnUiThread {
                            sendMemoryDetailsToServer(questionId.toString(), text)
                        }
                    } catch (e: JSONException) {
                        Log.e("UploadAudio", "JSON parsing error", e)
                    }
                } else {
                    Log.e("UploadAudio", "API call failed: ${response.code} - ${responseBody}")
                }
            }
        })
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

                        val fragment=SelectInstrumentFragment()
                        val bundle = Bundle()
                        bundle.putString("id", questionId)
                        fragment.arguments = bundle
                        parentFragmentManager.beginTransaction()
                            .replace(R.id.memory_frm,fragment)
                            .commit()

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

    override fun onDestroy() {
        super.onDestroy()
        mediaPlayer?.release()
        mediaPlayer = null
    }
}