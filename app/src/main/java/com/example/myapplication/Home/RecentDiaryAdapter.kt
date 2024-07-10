package com.example.myapplication.Home

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.myapplication.R

class RecentDiaryAdapter (private val diaryList: List<RecentDiaryData>) :
    RecyclerView.Adapter<RecentDiaryAdapter.DiaryViewHolder>() {

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DiaryViewHolder {
            val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.recent_item, parent, false)
            return DiaryViewHolder(view)
        }

    override fun onBindViewHolder(holder: DiaryViewHolder, position: Int) {
        val currentItem = diaryList[position]
        // 이미지 로딩 (Glide 사용 예시)

        /*Glide.with(holder.itemView.context)
            .load(currentItem.imageUrl)
            .error(R.drawable.post_sample)
            .into(holder.imageView)*/
        /*Glide.with(holder.itemView.context)
            .load(R.drawable.recent_card_sample)
            .error(R.drawable.post_sample)
            .into(holder.imageView)*/

        holder.imageView.setImageResource(currentItem.imageUrl)
    }

    override fun getItemCount(): Int {
        // 최대 3개의 아이템만 표시하도록 설정
        return minOf(diaryList.size, 3)
    }

    inner class DiaryViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val imageView: ImageView = itemView.findViewById(R.id.iv_diary_image)
    }

}
