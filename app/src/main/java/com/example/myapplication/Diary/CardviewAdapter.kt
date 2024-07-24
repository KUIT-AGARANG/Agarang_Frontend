package com.example.myapplication.Diary

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.myapplication.R
import com.example.myapplication.databinding.CardviewItemBinding
import com.example.myapplication.databinding.FragmentBottomSheetDialogBinding
import com.google.android.material.bottomsheet.BottomSheetDialog

class CardviewAdapter(
    private var items: MutableList<DiaryMainDayData>
) : RecyclerView.Adapter<CardviewAdapter.ViewHolder>() {

    inner class ViewHolder(val binding: CardviewItemBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = CardviewItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = items[position]
        holder.binding.apply {
            tvDate.text = "${item.year}.${item.month}.${item.day}"

            Glide.with(holder.itemView.context)
                .load(item.imageResId)
                .into(ivDiaryImage)
            tvContent.text = item.content

            ivBookmark.setImageResource(
                if (item.bookmark == 1) R.drawable.ic_heart_red
                else R.drawable.ic_heart_gray
            )

            ivBookmark.setOnClickListener {
                item.bookmark = if (item.bookmark == 0) 1 else 0
                ivBookmark.setImageResource(
                    if (item.bookmark == 1) R.drawable.ic_heart_red
                    else R.drawable.ic_heart_gray
                )
                notifyItemChanged(position)
            }

            ivOption.setOnClickListener {
                showOptionsBottomSheet(holder.itemView.context, item)
            }
        }
    }

    override fun getItemCount() = items.size

    private fun showOptionsBottomSheet(context: Context, item: DiaryMainDayData) {
        val bottomSheetDialog = BottomSheetDialog(context)
        val bottomSheetBinding = FragmentBottomSheetDialogBinding.inflate(LayoutInflater.from(context))

        bottomSheetBinding.apply {
            layoutEditOption.setOnClickListener {
                // 추억 수정 로직
                bottomSheetDialog.dismiss()
            }

            layoutDeleteOption.setOnClickListener {
                // 추억 지우기 로직
                bottomSheetDialog.dismiss()
            }

            layoutChangeNoteOption.setOnClickListener {
                // 노래 변경 로직
                bottomSheetDialog.dismiss()
            }

            layoutShareOption.setOnClickListener {
                // 카카오톡으로 공유 로직
                bottomSheetDialog.dismiss()
            }
        }

        bottomSheetDialog.setContentView(bottomSheetBinding.root)
        // BottomSheet의 너비를 조절
        bottomSheetDialog.show()
        bottomSheetDialog.window?.let { window ->
            val displayMetrics = context.resources.displayMetrics
            val width = (displayMetrics.widthPixels * 0.82).toInt() // 화면 너비의 82%
            window.setLayout(width, ViewGroup.LayoutParams.WRAP_CONTENT)
        }
    }
}