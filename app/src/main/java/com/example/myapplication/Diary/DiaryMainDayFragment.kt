package com.example.myapplication.Diary

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.GridLayoutManager
import com.example.myapplication.Data.Response.DailyMemory
import com.example.myapplication.Data.Response.DiaryDayResponse
import com.example.myapplication.R
import com.example.myapplication.Retrofit.DiaryIF
import com.example.myapplication.Retrofit.RetrofitService
import com.example.myapplication.databinding.FragmentDiaryMainDayBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class DiaryMainDayFragment : Fragment() {

    private lateinit var binding: FragmentDiaryMainDayBinding
    private var diaryDayAdapter: DiaryDayAdapter? = null
    private var diaryDayItemList: ArrayList<DiaryMainDayData> = arrayListOf()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentDiaryMainDayBinding.inflate(inflater, container, false)

        fetchDailyMemories()
        initRecyclerView()

        return binding.root
    }

    private fun fetchDailyMemories() {
        val service = RetrofitService.retrofit.create(DiaryIF::class.java)
        val dates = listOf("20240701", "20240702", "20240703", "20240704", "20240705")
        for (date in dates){
            service.getDailyMemories("daily", date).enqueue(object : Callback<DiaryDayResponse> {
                override fun onResponse(call: Call<DiaryDayResponse>, response: Response<DiaryDayResponse>) {
                    if (response.isSuccessful) {
                        val apiResponse = response.body()

                        if (apiResponse != null && apiResponse.isSuccess) {
                            updateRecyclerView(apiResponse.result.dailyMemories)
                        } else {
                            // 에러 처리
                            Log.e("오류", "API 요청이 성공하지 못했습니다: ${apiResponse?.message}")
                        }
                    } else {
                        // 오류 응답 처리
                        val errorBody = response.errorBody()?.string()
                        Log.e("오류", "Response error: $errorBody")
                    }
                }

                override fun onFailure(call: Call<DiaryDayResponse>, t: Throwable) {
                    Log.e("실패", "API 요청 실패: ${t.message}")
                }
            })
        }
    }

    private fun updateRecyclerView(memories: List<DailyMemory>) {
        diaryDayItemList.addAll(memories.map { memory ->
            DiaryMainDayData(
                id = memory.id,
                date = memory.date,
                imageResId = memory.imageUrl
            )
        })
        diaryDayAdapter?.notifyDataSetChanged()
    }

    private fun initRecyclerView() {
        val spanCount = 3
        diaryDayAdapter = DiaryDayAdapter(requireContext(), diaryDayItemList)
        binding.rvDiaryDay.apply {
            adapter = diaryDayAdapter
            layoutManager = GridLayoutManager(context, spanCount)
            addItemDecoration(SquareItemDecoration(spanCount))
        }

        diaryDayAdapter?.setOnItemClickListener(object : DiaryDayAdapter.OnItemClickListener {
            override fun onItemClick(item: DiaryMainDayData) {
                navigateToDiaryMainCard(item)
            }
        })
    }

    private fun navigateToDiaryMainCard(item: DiaryMainDayData) {
        val fragment = DiaryMainCardFragment()
        val bundle = Bundle().apply {
            putSerializable("data", ArrayList(diaryDayItemList))
            putInt("position", diaryDayItemList.indexOf(item))
        }
        fragment.arguments = bundle
        parentFragmentManager.beginTransaction().replace(R.id.main_frm, fragment).addToBackStack(null)
            .commit()
    }


}