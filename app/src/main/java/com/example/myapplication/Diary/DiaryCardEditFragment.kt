package com.example.myapplication.Diary

import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.bumptech.glide.Glide
import com.example.myapplication.Data.Request.EditMemoryRequest
import com.example.myapplication.Data.Response.EditMemoryResponse
import com.example.myapplication.Retrofit.DiaryIF
import com.example.myapplication.Retrofit.RetrofitService
import com.example.myapplication.databinding.FragmentDiaryCardEditBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class DiaryCardEditFragment : Fragment() {

    private lateinit var binding: FragmentDiaryCardEditBinding
    private lateinit var item: DiaryMainCardData
    private var position: Int = -1

    companion object {
        fun newInstance(item: DiaryMainCardData, position: Int): DiaryCardEditFragment {
            val fragment = DiaryCardEditFragment()
            val args = Bundle().apply {
                putSerializable("item", item)
                putInt("position", position)
            }
            fragment.arguments = args
            return fragment
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            item = it.getSerializable("item") as DiaryMainCardData
            position = it.getInt("position")
        }
    }

    interface OnEditCompleteListener {
        fun onEditComplete(position: Int, editedItem: DiaryMainCardData)
    }

    private var editCompleteListener: OnEditCompleteListener? = null

    fun setOnEditCompleteListener(listener: OnEditCompleteListener) {
        this.editCompleteListener = listener
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = FragmentDiaryCardEditBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        context?.let {
            Glide.with(it)
                .load(item.imageUrl)
                .centerCrop()
                .into(binding.ivCardEditImage)
        }

        binding.tvDiaryCardEditContent.setText(item.content)
        binding.tvDiaryCardEditContent.requestFocus()

        binding.tvDiaryCardEditComplete.setOnClickListener {
            val editedContent = binding.tvDiaryCardEditContent.text.toString()
            updateMemory(item.id.toLong(), editedContent)
        }
    }

    private fun updateMemory(memoryId: Long, newContent: String) {
        val apiService = RetrofitService.createRetrofit(requireContext()).create(DiaryIF::class.java)
        val request = EditMemoryRequest(memoryId, newContent)

        apiService.editMemory(request).enqueue(object : Callback<EditMemoryResponse> {
            override fun onResponse(call: Call<EditMemoryResponse>, response: Response<EditMemoryResponse>) {
                if (response.isSuccessful) {
                    val result = response.body()
                    if (result != null && result.isSuccess) {
                        Log.d("Edit", "메모리 수정 성공")

                        val editedItem = item.copy(content = newContent)
                        editCompleteListener?.onEditComplete(position, editedItem)
                        parentFragmentManager.popBackStack()
                    } else {
                        Log.e("Edit", "메모리 수정 실패: ${result?.message}")
                    }
                } else {
                    Log.e("Edit", "메모리 수정 실패: ${response.code()}")
                }
            }

            override fun onFailure(call: Call<EditMemoryResponse>, t: Throwable) {
                Log.e("Edit", "서버 통신 실패", t)
            }
        })
    }
}