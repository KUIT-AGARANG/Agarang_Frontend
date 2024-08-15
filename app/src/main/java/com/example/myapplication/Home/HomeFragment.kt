package com.example.myapplication.Home

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.myapplication.R
import com.example.myapplication.Setting.HomeSettingFragment
import com.example.myapplication.SharedViewModel
import com.example.myapplication.databinding.FragmentHomeBinding
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class HomeFragment: Fragment() {

    lateinit var binding: FragmentHomeBinding
    private var RecentDiaryAdapter: RecentDiaryAdapter?= null
    private var RecentDiaryDataList : ArrayList<RecentDiaryData> = arrayListOf()
    private val sharedViewModel: SharedViewModel by activityViewModels()
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentHomeBinding.inflate(inflater, container, false)

        initData()
        initRecyclerView()

        binding.ivSetting.setOnClickListener {
            val fragmentSetting=HomeSettingFragment()
            val transaction=parentFragmentManager.beginTransaction()
            transaction.replace(R.id.main_frm,fragmentSetting)
                .commit()
        }

        // 현재 날짜 가져오기
        val calendar = Calendar.getInstance()
        val currentDate = calendar.time
        // 날짜를 특정 형식의 문자열로 변환
        val dateFormat = SimpleDateFormat("yyyy년 MM월 dd일", Locale.getDefault())
        val formattedDate = dateFormat.format(currentDate)
        binding.tvToday.text = formattedDate

        val sharedPreferences = requireContext().getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
        val selectedChar = sharedPreferences.getInt("selected_char", -1)

        if (selectedChar != -1) {
            // selectedChar 값을 사용하여 작업 수행
            binding.ivBabyTiger.setImageResource(selectedChar)
        }

        return binding.root
    }

    private fun initRecyclerView(){
        val spanCount = 3 // 열의 수
        RecentDiaryAdapter = RecentDiaryAdapter(RecentDiaryDataList)
        binding.rvRecentCard.adapter = RecentDiaryAdapter
        binding.rvRecentCard.layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)

        /*val itemDecoration = DividerItemDecoration(context, DividerItemDecoration.HORIZONTAL)
        binding.rvRecentCard.addItemDecoration(itemDecoration)*/
        /*binding.rvRecentCard.layoutManager = GridLayoutManager(context, spanCount)
        binding.rvRecentCard.addItemDecoration(DividerItemDecoration(context, DividerItemDecoration.VERTICAL))
        binding.rvRecentCard.addItemDecoration(DividerItemDecoration(context, DividerItemDecoration.HORIZONTAL))*/
    }

    private fun initData(){
        sharedViewModel.babyName.observe(viewLifecycleOwner){ name->
            binding.tvDDayText.setText(name+"가 태어나기까지")
        }
        sharedViewModel.babyDday.observe(viewLifecycleOwner){ Dday->
            if(Dday>0){ binding.tvDDay.setText("D-"+Dday) }
            else if (Dday==0) { binding.tvDDay.setText("D-Day") }
            else { binding.tvDDay.setText("") }

        }
        RecentDiaryDataList.addAll(
            arrayListOf(
                RecentDiaryData("내용1", R.drawable.recent_card_sample),
                RecentDiaryData("내용2", R.drawable.recent_card_sample),
                RecentDiaryData("내용3", R.drawable.recent_card_sample)
            )
        )
    }
}