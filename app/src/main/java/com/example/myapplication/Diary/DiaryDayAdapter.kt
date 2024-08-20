package com.example.myapplication.Diary

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.myapplication.databinding.GridDayItemBinding

class DiaryDayAdapter (val context: Context, val items: ArrayList<DiaryMainDayData>) : RecyclerView.Adapter<DiaryDayAdapter.ViewHolder>() {

    interface OnItemClickListener {
        fun onItemClick(item: DiaryMainDayData)
    }

    private var listener: OnItemClickListener? = null

    fun setOnItemClickListener(listener: OnItemClickListener) {
        this.listener = listener
    }

    inner class ViewHolder(val binding: GridDayItemBinding) : RecyclerView.ViewHolder(binding.root){
        fun bind(item : DiaryMainDayData){
            binding.tvDay.text = item.day.toString()

            Glide.with(context)
                .load(item.imageResId)
                .centerCrop()
                .into(binding.ivDay)

            itemView.setOnClickListener {
                listener?.onItemClick(item)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = GridDayItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        Log.d("DiaryDayAdapter", "Binding item at position $position")
        holder.bind(items[position])
    }

    override fun getItemCount(): Int = items.size

}