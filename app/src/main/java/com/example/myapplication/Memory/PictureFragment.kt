package com.example.myapplication.Memory

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.example.myapplication.R
import com.example.myapplication.databinding.FragmentPictureBinding
import com.example.myapplication.Data.Response.MemoryImageToQuestionResponse
import com.example.myapplication.Retrofit.MemoryIF
import com.example.myapplication.Retrofit.RetrofitService
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.asRequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream

class PictureFragment : Fragment() {
    private lateinit var binding: FragmentPictureBinding
    private lateinit var galleryLauncher: ActivityResultLauncher<Intent>

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentPictureBinding.inflate(inflater, container, false)

        galleryLauncher = registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()
        ) { result ->
            if (result.resultCode == AppCompatActivity.RESULT_OK) {
                val selectedImageUri: Uri? = result.data?.data ?: return@registerForActivityResult

                selectedImageUri?.let { uri ->
                    val compressedFile = compressImage(uri)
                    val requestFile = compressedFile.asRequestBody("image/*".toMediaTypeOrNull())
                    val body = MultipartBody.Part.createFormData("image", compressedFile.name, requestFile)

                    sendImageToServer(body)
                }
            }
        }

        binding.ivGallery.setOnClickListener {
            openGallery()
        }

        val sharedPreferences =
            requireContext().getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
        val selectedChar = sharedPreferences.getInt("selected_char", -1)

        if (selectedChar != -1) {
            binding.ivBabyCharacter.setImageResource(selectedChar)
        }

        return binding.root
    }

    private fun openGallery() {
        val intent = Intent(Intent.ACTION_PICK).apply {
            type = "image/*"
        }
        galleryLauncher.launch(intent)
    }

    private fun compressImage(uri: Uri): File {
        val inputStream = requireContext().contentResolver.openInputStream(uri)
        val bitmap = BitmapFactory.decodeStream(inputStream)
        val compressedFile = File(requireContext().cacheDir, "compressed_image.jpg")

        var outputStream: FileOutputStream? = null
        try {
            outputStream = FileOutputStream(compressedFile)
            bitmap.compress(Bitmap.CompressFormat.JPEG, 30, outputStream) // 품질을 30%로 압축
        } finally {
            outputStream?.close()
        }

        return compressedFile
    }

    private fun sendImageToServer(body: MultipartBody.Part) {
        val service = RetrofitService.createRetrofit(requireContext()).create(MemoryIF::class.java)
        service.sendImageToQuestion(body).enqueue(object : Callback<MemoryImageToQuestionResponse> {
            override fun onResponse(
                call: Call<MemoryImageToQuestionResponse>,
                response: Response<MemoryImageToQuestionResponse>
            ) {
                if (response.isSuccessful) {
                    val apiResponse = response.body()
                    if (apiResponse != null && apiResponse.isSuccess) {
                        Log.d("PictureFragment", "성공: ${apiResponse.message}")
                        // 응답 처리 (예: 다음 Fragment로 이동)
                        val fragment = PicAssociationFragment()
                        val bundle = Bundle()
                        bundle.putString("id", apiResponse.result.question.id)
                        bundle.putString("question", apiResponse.result.question.text)
                        bundle.putString("audioUrl", apiResponse.result.question.audioUrl)
                        fragment.arguments = bundle

                        parentFragmentManager.beginTransaction()
                            .replace(R.id.memory_frm, fragment)
                            .commit()
                    } else {
                        Log.e("PictureFragment", "에러: ${apiResponse?.code}")
                    }
                } else {
                    Log.e("PictureFragment", "응답 오류: ${response.errorBody()?.string()}")
                }
            }

            override fun onFailure(call: Call<MemoryImageToQuestionResponse>, t: Throwable) {
                Log.e("PictureFragment", "실패: ${t.message}")
            }
        })
    }
}