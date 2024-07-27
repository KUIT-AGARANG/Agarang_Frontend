package com.example.myapplication.Memory

import android.Manifest
import android.app.VoiceInteractor
import android.content.pm.PackageManager
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
import com.example.myapplication.R
import com.example.myapplication.databinding.FragmentPicAssociationBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import okhttp3.Call
import okhttp3.Callback
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.Response
import org.json.JSONException
import org.json.JSONObject
import java.io.File
import java.io.IOException

class PicAssociationFragment : Fragment() {
    private lateinit var binding:FragmentPicAssociationBinding
    private var mediaRecorder: MediaRecorder? = null
    private var audioFile: File? = null

    private val clientId = "nlpxphm34l"
    private val clientSecret = "B4F7SeFMWV7UpjzOcuu6Kb0nsEuz8EUaF6HYOL44"
    /*private val apiUrl = "https://naveropenapi.apigw.ntruss.com/recog/v1/stt"*/

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding=FragmentPicAssociationBinding.inflate(inflater,container,false)

        // 녹음 권한 요청
        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED ||
            ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(requireActivity(), arrayOf(Manifest.permission.RECORD_AUDIO, Manifest.permission.WRITE_EXTERNAL_STORAGE), 1)
        }

        binding.ivRecordBtn.setOnClickListener {
            startRecording()
            binding.ivRecordBtn.visibility=View.GONE
            binding.ivRecordCancleBtn.visibility=View.VISIBLE
            binding.ivRecordArrowBtn.visibility=View.VISIBLE
            binding.ivRecordIng.visibility=View.VISIBLE
            binding.ivRecordingNextBtn.visibility=View.GONE
        }
        binding.ivRecordingNextBtn.setOnClickListener {
            val fragment=DeepQuestionFragment()
            parentFragmentManager.beginTransaction()
                .replace(R.id.memory_frm,fragment)
                .commit()
        }

        binding.ivRecordCancleBtn.setOnClickListener {
            stopRecording()
            binding.ivRecordBtn.visibility=View.VISIBLE
            binding.ivRecordCancleBtn.visibility=View.GONE
            binding.ivRecordArrowBtn.visibility=View.GONE
            binding.ivRecordIng.visibility=View.GONE
            binding.ivRecordingNextBtn.visibility=View.GONE
        }

        binding.ivRecordArrowBtn.setOnClickListener {
            stopRecording()
            binding.ivRecordBtn.visibility=View.GONE
            binding.ivRecordCancleBtn.visibility=View.GONE
            binding.ivRecordArrowBtn.visibility=View.GONE
            binding.ivRecordIng.visibility=View.GONE
            binding.ivRecordingNextBtn.visibility=View.VISIBLE

            uploadAudioFileAndGetText()
        }

        return binding.root
    }
    private fun startRecording() {
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
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
    }
    private fun stopRecording() {
        mediaRecorder?.apply {
            stop()
            release()
        }
        mediaRecorder = null
    }

    private fun uploadAudioFileAndGetText() {
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

        client.newCall(request).enqueue(object : Callback {
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
                        activity?.runOnUiThread {
                            // UI 업데이트
                            binding.tvRecordNotice.text = "text"
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
}