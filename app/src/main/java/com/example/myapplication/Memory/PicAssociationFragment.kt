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
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.myapplication.Data.Request.FirstAnsRequest
import com.example.myapplication.Data.Response.FirstAnsResponse
import com.example.myapplication.R
import com.example.myapplication.Retrofit.MemoryIF
import com.example.myapplication.Retrofit.RetrofitService
import com.example.myapplication.databinding.FragmentPicAssociationBinding
import okhttp3.Call
import okhttp3.Callback
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.Response
import org.json.JSONException
import org.json.JSONObject
import java.io.File
import java.io.IOException
import retrofit2.Callback as RetrofitCallback
import retrofit2.Call as RetrofitCall



class PicAssociationFragment : Fragment() {
    private lateinit var binding:FragmentPicAssociationBinding
    private var mediaRecorder: MediaRecorder? = null
    private var audioFile: File? = null
    private var mediaPlayer: MediaPlayer? = null

    val nextfragment=DeepQuestionFragment()

    private val clientId = "nlpxphm34l"
    private val clientSecret = "B4F7SeFMWV7UpjzOcuu6Kb0nsEuz8EUaF6HYOL44"

    // 변수 선언 MemoryResponse - FirstQuestion

    private var questionId: String? = null
    private var questionText: String? = null
    private var audioUrl: String? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding=FragmentPicAssociationBinding.inflate(inflater,container,false)

        // 번들로 전달된 데이터 가져오기
        arguments?.let { bundle ->
            questionId = bundle.getString("id")
            questionText = bundle.getString("question")
            audioUrl = bundle.getString("audioUrl")

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

        // 오디오 URL을 자동으로 스트리밍
        if (audioUrl != null) {
            playAudio()
        }

        binding.ivRecordBtn.setOnClickListener {
            Log.d("PicAssociationFragment", "Record button clicked")
            startRecording()
            binding.ivRecordBtn.visibility=View.GONE
            binding.ivRecordCancleBtn.visibility=View.VISIBLE
            binding.ivRecordArrowBtn.visibility=View.VISIBLE
            binding.ivRecordIng.visibility=View.VISIBLE
            binding.ivRecordingNextBtn.visibility=View.GONE
        }


        binding.ivRecordCancleBtn.setOnClickListener {
            Log.d("PicAssociationFragment", "Cancel button clicked")
            stopRecording()
            binding.ivRecordBtn.visibility=View.VISIBLE
            binding.ivRecordCancleBtn.visibility=View.GONE
            binding.ivRecordArrowBtn.visibility=View.GONE
            binding.ivRecordIng.visibility=View.GONE
            binding.ivRecordingNextBtn.visibility=View.GONE
        }

        binding.ivRecordArrowBtn.setOnClickListener {
            Log.d("PicAssociationFragment", "Arrow button clicked")
            stopRecording()
            binding.ivRecordBtn.visibility=View.GONE
            binding.ivRecordCancleBtn.visibility=View.GONE
            binding.ivRecordArrowBtn.visibility=View.GONE
            binding.ivRecordIng.visibility=View.GONE
            binding.ivRecordingNextBtn.visibility=View.VISIBLE

            uploadAudioFileAndGetText()
        }

        val sharedPreferences = requireContext().getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
        val selectedChar = sharedPreferences.getInt("selected_char", -1)

        if (selectedChar != -1) {
            // selectedChar 값을 사용하여 작업 수행
            binding.ivBabyCharacter.setImageResource(selectedChar)
            Log.d("PicAssociationFragment", "Character selected: $selectedChar")
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
            override fun onFailure(call: Call, e: IOException) {
                Log.e("UploadAudio", "API call failed", e)
            }

            override fun onResponse(call: Call, response: Response) {
                val responseBody = response.body?.string()
                if (response.isSuccessful && responseBody != null) {
                    Log.d("성공", "API Response: ${responseBody}")
                    try {
                        val jsonObject = JSONObject(responseBody)
                        val text = jsonObject.optString("text")
                        Log.d("성공", "Extracted text: $text")
                        activity?.runOnUiThread {

                            //server
                            val apiService = RetrofitService.createRetrofit(requireContext()).create(MemoryIF::class.java)
                            val request=FirstAnsRequest(
                                id=questionId.toString(),
                                text = text
                            )
                            apiService.sendFirstAns(request).enqueue(object : retrofit2.Callback<FirstAnsResponse>{
                                override fun onResponse(
                                    call: retrofit2.Call<FirstAnsResponse>,
                                    response: retrofit2.Response<FirstAnsResponse>
                                ) {
                                    if (response.isSuccessful){
                                        val result=response.body()
                                        result?.let{
                                            Log.d("Tag",result.toString())
                                            //심화 fragment 로 넘어가야 함 -> 응답으로 온 audioUrl을 음성으로 내보내고, text ui에 띄우기

                                            //번들로 전달 (DeepQuestion으로)
                                            val bundle = Bundle().apply {
                                                putString("id", result.result.question.id)
                                                putString("text", result.result.question.text)
                                                putString("audioUrl", result.result.question.audioUrl)
                                            }
                                            Log.d("bundle",result.result.question.text)
                                            nextfragment.arguments = bundle

                                            parentFragmentManager.beginTransaction()
                                                .replace(R.id.memory_frm,nextfragment)
                                                .commit()
                                        }
                                    }else{
                                        Log.e("PicAssociationFragment","Failed to get response")
                                    }
                                }
                                override fun onFailure(call: retrofit2.Call<FirstAnsResponse>, t: Throwable) {
                                    Log.e("PicAssociationFragment","API call failed",t)
                                }
                            })


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


    private fun playAudio() {
        if (audioUrl == null) {
            Log.e("PicAssociationFragment", "Audio URL is null")
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