package com.example.myapplication.Home

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.myapplication.R
import com.example.myapplication.databinding.RecentItemBinding

class RecentDiaryAdapter (
    private val diaryList: List<RecentDiaryData>,
    private val onItemClick: (Int) -> Unit) :

/*RecyclerView.Adapter<RecentDiaryAdapter.DiaryViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DiaryViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.recent_item, parent, false)
        return DiaryViewHolder(view)
    }

    override fun onBindViewHolder(holder: DiaryViewHolder, position: Int) {
        val currentItem = diaryList[position]
        // 이미지 로딩
        Glide.with(holder.itemView.context)
            .load(currentItem.imageUrl)
            .error(R.drawable.post_sample)
            .into(holder.imageView)

    }

    override fun getItemCount(): Int {
        // 최대 3개의 아이템만 표시하도록 설정
        return minOf(diaryList.size, 3)
    }

    inner class DiaryViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val imageView: ImageView = itemView.findViewById(R.id.iv_diary_image)
    }*/

    RecyclerView.Adapter<RecentDiaryAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = RecentItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(diaryList[position])
    }

    override fun getItemCount(): Int = diaryList.size

    inner class ViewHolder(private val binding: RecentItemBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(diary: RecentDiaryData) {
            Glide.with(binding.root.context).load(diary.imageUrl).into(binding.ivDiaryImage)

            // 아이템 클릭 리스너 설정
            itemView.setOnClickListener {
                onItemClick(diary.id)
            }
        }
    }

}
