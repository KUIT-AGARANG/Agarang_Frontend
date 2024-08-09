package com.example.myapplication.Diary

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.myapplication.R
import com.example.myapplication.databinding.CardviewItemBinding
import com.example.myapplication.databinding.FragmentBottomSheetDialogBinding
import com.google.android.material.bottomsheet.BottomSheetDialog

class CardViewAdapter(
    private var items: MutableList<DiaryMainDayData>,
    private val onItemDeleted: (DiaryMainDayData) -> Unit,
    private val onBookmarkClicked: (Long)->Unit
) : RecyclerView.Adapter<CardViewAdapter.ViewHolder>(), DiaryCardEditFragment.OnEditCompleteListener {

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
                if (item.favorite) R.drawable.ic_heart_red
                else R.drawable.ic_heart_gray
            )

            ivBookmark.setOnClickListener {
                val updatedFavoriteStatus = !item.favorite
                item.favorite = updatedFavoriteStatus

                ivBookmark.setImageResource(
                    if (updatedFavoriteStatus) R.drawable.ic_heart_red
                    else R.drawable.ic_heart_gray
                )
                onBookmarkClicked(item.id.toLong())
                notifyItemChanged(position)
                onItemDeleted(item)
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
                val editFragment = DiaryCardEditFragment.newInstance(item, items.indexOf(item))
                editFragment.setOnEditCompleteListener(this@CardViewAdapter)
                val fragmentManager = (context as FragmentActivity).supportFragmentManager
                fragmentManager.beginTransaction()
                    .replace(R.id.main_frm, editFragment)
                    .addToBackStack(null)
                    .commit()
                bottomSheetDialog.dismiss()
            }

            layoutDeleteOption.setOnClickListener {
                showDeleteConfirmationDialog(context, item)
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
        bottomSheetDialog.show()
        bottomSheetDialog.window?.let { window ->
            val displayMetrics = context.resources.displayMetrics
            val width = (displayMetrics.widthPixels * 0.8).toInt()
            window.setLayout(width, ViewGroup.LayoutParams.WRAP_CONTENT)
        }
    }

    override fun onEditComplete(position: Int, editedItem: DiaryMainDayData) {
        if (position in 0 until items.size) {
            items[position] = editedItem
            notifyItemChanged(position)
        }
    }

    private fun showDeleteConfirmationDialog(context: Context, item: DiaryMainDayData) {
        val fragmentManager = (context as FragmentActivity).supportFragmentManager
        val dialogFragment = DiaryDeleteDialogFragment()

        dialogFragment.onDeleteConfirmed = {
            deleteMemory(item)
        }
        dialogFragment.show(fragmentManager, DiaryDeleteDialogFragment.TAG)
    }



    private fun deleteMemory(item: DiaryMainDayData) {
        val position = items.indexOf(item)
        if (position != -1) {
            items.removeAt(position)
            notifyItemRemoved(position)
            onItemDeleted(item)  // 삭제 시 콜백 호출
            // 여기에 데이터베이스나 서버에서 실제로 데이터를 삭제하는 로직을 추가할 수 있습니다.
        }
    }
}