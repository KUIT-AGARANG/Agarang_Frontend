package com.example.myapplication.Diary

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.myapplication.R
import com.example.myapplication.databinding.DateItemBinding
import java.util.*

class DateAdapter(
    private var year: Int,
    private var month: Int,
    private var items: List<DiaryMainCardData>,
    //클릭된 날짜를 전달하는 콜백 함수 ... Int를 String으로 변경함
    private val onItemClick: (Int) -> Unit
) : RecyclerView.Adapter<DateAdapter.ViewHolder>() {

    var selectedPosition = RecyclerView.NO_POSITION
    private val calendar: Calendar = Calendar.getInstance()
    private val daysInMonth: Int

    init {
        calendar.set(year, month - 1, 1)
        daysInMonth = calendar.getActualMaximum(Calendar.DAY_OF_MONTH)
    }

    inner class ViewHolder(val binding: DateItemBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = DateItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val day = position + 1
        val diaryItem = items.find { it.day == day }

        holder.binding.apply {
            tvWeekday.text = getWeekday(year, month, day)

            if (position == selectedPosition) {
                ivDiaryThumbnail.visibility = View.GONE
                tvDay.visibility = View.VISIBLE
                tvDayBelow.visibility = View.GONE
                tvDay.text = day.toString()
                tvDay.setTextColor(ContextCompat.getColor(holder.itemView.context, R.color.selected_date_color))
                tvDay.setBackgroundResource(R.drawable.selected_date_background)
            } else if (diaryItem != null) {
                ivDiaryThumbnail.visibility = View.VISIBLE
                tvDay.visibility = View.GONE
                tvDayBelow.visibility = View.VISIBLE
                tvDayBelow.text = day.toString()
                /*Glide.with(holder.itemView.context)
                    .load(diaryItem.imageResId)
                    .into(ivDiaryThumbnail)*/
                Glide.with(holder.itemView.context)
                    .load(diaryItem.imageUrl)
                    .into(ivDiaryThumbnail)
            } else {
                ivDiaryThumbnail.visibility = View.GONE
                tvDay.visibility = View.VISIBLE
                tvDayBelow.visibility = View.GONE
                tvDay.text = day.toString()
                tvDay.setBackgroundResource(android.R.color.transparent)
                tvDay.setTextColor(ContextCompat.getColor(holder.itemView.context, android.R.color.black))
            }

            root.setOnClickListener {
                if (diaryItem != null) {
                    val diaryItemPosition = items.indexOf(diaryItem)
                    onItemClick(diaryItemPosition)
                    updateSelectedPosition(position)
                }

                // 날짜 클릭 시 처리
                /*val selectedDate = String.format("%04d%02d%02d", year, month, day)
                onItemClick(selectedDate) // 클릭된 날짜를 콜백을 통해 전달
                updateSelectedPosition(position)*/
            }
        }
    }

    fun updateSelectedPosition(position: Int) {
        val previousPosition = selectedPosition
        selectedPosition = position
        notifyItemChanged(previousPosition)
        notifyItemChanged(selectedPosition)
    }

    fun updateItems(newItems: List<DiaryMainCardData>) {
        items = newItems
        notifyDataSetChanged()
    }

    override fun getItemCount() = daysInMonth // 해당 월의 날짜 수만큼 리턴

    // 요일 계산
    private fun getWeekday(year: Int, month: Int, day: Int): String {
        calendar.set(year, month - 1, day)
        return when (calendar.get(Calendar.DAY_OF_WEEK)) {
            Calendar.SUNDAY -> "일"
            Calendar.MONDAY -> "월"
            Calendar.TUESDAY -> "화"
            Calendar.WEDNESDAY -> "수"
            Calendar.THURSDAY -> "목"
            Calendar.FRIDAY -> "금"
            Calendar.SATURDAY -> "토"
            else -> ""
        }
    }
}