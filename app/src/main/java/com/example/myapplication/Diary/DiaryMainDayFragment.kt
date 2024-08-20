package com.example.myapplication.Diary

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.replace
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.myapplication.Data.Response.DailyMemory
import com.example.myapplication.Data.Response.DiaryDayResponse
import com.example.myapplication.R
import com.example.myapplication.Retrofit.DiaryIF
import com.example.myapplication.Retrofit.RetrofitService
import com.example.myapplication.databinding.FragmentDiaryMainDayBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class DiaryMainDayFragment : Fragment() {

    private lateinit var binding: FragmentDiaryMainDayBinding
    private var diaryDayAdapter: DiaryDayAdapter? = null
    private var diaryDayItemList: ArrayList<DiaryMainDayData> = arrayListOf()
    private var selectedDate: String? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentDiaryMainDayBinding.inflate(inflater, container, false)

        initRecyclerView()
        setupScrollListener()

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        arguments?.let { args ->
            val year = args.getInt("year", -1)
            val month = args.getInt("month", -1)
            if (year != -1 && month != -1) {
                selectedDate = String.format("%04d-%02d", year, month)
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // 결과 리스너를 설정합니다.
        parentFragmentManager.setFragmentResultListener("refresh_diary_day", this) { _, bundle ->
            val shouldRefresh = bundle.getBoolean("refresh", false)
            if (shouldRefresh) {
                fetchDailyMemories()
            }
        }
    }

    override fun onResume() {
        super.onResume()
        // 화면이 다시 보일 때마다 데이터를 새로 가져옵니다.
        fetchDailyMemories()
    }

    private fun fetchDailyMemories() {
        val service = RetrofitService.createRetrofit(requireContext()).create(DiaryIF::class.java)

        service.getDailyMemories("daily").enqueue(object : Callback<DiaryDayResponse> {
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

    private fun updateRecyclerView(memories: List<DailyMemory>) {
        diaryDayItemList.clear()
        diaryDayItemList.addAll(memories.map { memory ->
            DiaryMainDayData(
                id = memory.id,
                date = memory.date,
                imageResId = memory.imageUrl ?: ""
            )
        })
        diaryDayAdapter?.notifyDataSetChanged()

        if (selectedDate != null) {
            Log.d("scroll", "$selectedDate")
            scrollToFirstItemOfMonth()
        } else {
            scrollToLastItem()
        }
    }

    private fun scrollToFirstItemOfMonth() {
        if (diaryDayItemList.isNotEmpty() && selectedDate != null) {
            val targetDate = selectedDate!!
            Log.d("Scroll", "Trying to scroll to date: $targetDate")

            val position = diaryDayItemList.indexOfFirst { item ->
                val itemDate = item.date.replace("-", "")  // 하이픈 제거
                itemDate.startsWith(targetDate.replace("-", ""))
            }

            Log.d("Scroll", "Found position: $position")

            if (position != -1) {
                binding.rvDiaryDay.post {
                    (binding.rvDiaryDay.layoutManager as? GridLayoutManager)?.let { layoutManager ->
                        // 첫 번째 파라미터는 스크롤할 위치, 두 번째 파라미터는 상단 오프셋(0으로 설정하면 화면 최상단에 위치)
                        layoutManager.scrollToPositionWithOffset(position, 0)
                        updateDateText() // 스크롤 후 날짜 텍스트 업데이트
                        Log.d("Scroll", "Scrolled to position: $position")
                    }
                }
            } else {
                Log.d("Scroll", "No matching item found for date: $targetDate")
            }
        } else {
            Log.d("Scroll", "List is empty or selectedDate is null")
        }
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
    private fun scrollToLastItem() {
        // 아이템이 있는 경우에만 스크롤
        if (diaryDayItemList.isNotEmpty()) {
            binding.rvDiaryDay.scrollToPosition(diaryDayItemList.size - 1)
        }
    }

    private fun navigateToDiaryMainCard(item: DiaryMainDayData) {
        val fragment = DiaryMainCardFragment()

        // 날짜에서 월 추출 (예: "2023-08-19"에서 8월 추출)
        val selectedMonth = extractMonth(item.date)

        // 선택한 월에 속하는 아이템들만 필터링
        val filteredList = diaryDayItemList.filter { extractMonth(it.date) == selectedMonth }

        // 선택한 아이템의 월 내 인덱스 구하기
        val positionInMonth = filteredList.indexOf(item)

        val bundle = Bundle().apply {
            putSerializable("data", ArrayList(diaryDayItemList))
            Log.d("position", "$positionInMonth")
            putInt("position", positionInMonth)
            putString("date", item.date) // 선택한 아이템의 날짜 전달
        }
        fragment.arguments = bundle
        parentFragmentManager.beginTransaction().replace(R.id.main_frm, fragment).addToBackStack(null)
            .commit()
    }


    private fun extractMonth(date: String): Int {
        // 날짜 형식에 맞게 SimpleDateFormat 정의 (예: "yyyy-MM-dd")
        val dateFormat = SimpleDateFormat("yyyyMMdd", Locale.getDefault())
        val parsedDate = dateFormat.parse(date)
        val calendar = Calendar.getInstance().apply { time = parsedDate }
        return calendar.get(Calendar.MONTH) + 1 // 월은 0부터 시작하므로 +1 해줌
    }

    private fun setupScrollListener() {
        binding.rvDiaryDay.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                updateDateText()
            }
        })
    }

    private fun updateDateText() {
        val layoutManager = binding.rvDiaryDay.layoutManager as? GridLayoutManager ?: return
        val firstVisibleItemPosition = layoutManager.findFirstVisibleItemPosition()
        if (firstVisibleItemPosition != RecyclerView.NO_POSITION && diaryDayItemList.isNotEmpty()) {
            val item = diaryDayItemList[firstVisibleItemPosition]
            val date = item.date
            val formattedDate = formatDate(date)
            binding.tvDate.text = formattedDate
        }
    }

    private fun formatDate(date: String): String {
        // date 형식이 "yyyyMMdd"라고 가정
        val year = date.substring(0, 4)
        val month = date.substring(4, 6)
        return "$year / $month"
    }


}