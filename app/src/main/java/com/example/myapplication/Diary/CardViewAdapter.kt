package com.example.myapplication.Diary

import android.content.Context
import android.media.MediaPlayer
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.myapplication.R
import com.example.myapplication.databinding.CardviewItemBinding
import com.example.myapplication.databinding.FragmentBottomSheetDialogBinding
import com.google.android.material.bottomsheet.BottomSheetDialog
import java.io.IOException

class CardViewAdapter(
    private var items: MutableList<DiaryMainCardData>,
    private val onItemDeleted: (Long, DiaryMainCardData) -> Unit,
    private val onBookmarkClicked: (Long)->Unit
) : RecyclerView.Adapter<CardViewAdapter.ViewHolder>(), DiaryCardEditFragment.OnEditCompleteListener {

    inner class ViewHolder(val binding: CardviewItemBinding) : RecyclerView.ViewHolder(binding.root)
    private var mediaPlayer: MediaPlayer? = null
    private var audioUrl: String? = null
    private var isPlaying=false

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = CardviewItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = items[position]
        holder.binding.apply {
            tvDate.text = "${item.year}.${item.month}.${item.day}"

            Glide.with(holder.itemView.context)
                .load(item.imageUrl)
                .into(ivDiaryImage)
            tvContent.text = item.content
            tvWriter.text = item.writer

            if(item.musicUrl.equals("")){
                tvMusicTitle.text = "음악이 생성되는 중입니다."
            } else {
                tvMusicTitle.text = item.musicTitle
                ivDiaryImage.setOnClickListener {
                   audioUrl = item.musicUrl
                    if (mediaPlayer==null||!isPlaying){
                        playAudio()
                    }else{
                        mediaPlayer?.pause()
                        isPlaying=false
                    }
                    mediaPlayer?.setOnCompletionListener {
                        resetAudio()
                    }
                }
            }


            ivBookmark.setImageResource(
                if (item.bookmarked) R.drawable.ic_heart_red
                else R.drawable.ic_heart_gray
            )

            ivBookmark.setOnClickListener {
                val updatedFavoriteStatus = !item.bookmarked
                item.bookmarked = updatedFavoriteStatus

                ivBookmark.setImageResource(
                    if (updatedFavoriteStatus) R.drawable.ic_heart_red
                    else R.drawable.ic_heart_gray
                )
                onBookmarkClicked(item.id.toLong())
                notifyItemChanged(position)
            }

            ivOption.setOnClickListener {
                showOptionsBottomSheet(holder.itemView.context, item)
            }
            // 해시태그 설정
            val hashTags = item.hashTags

            tvHashtag1.text = hashTags.getOrNull(0)?.let { "#$it" } ?: ""  // 첫 번째 해시태그
            tvHashtag2.text = hashTags.getOrNull(1)?.let { "#$it" } ?: ""  // 두 번째 해시태그
            tvHashtag3.text = hashTags.getOrNull(2)?.let { "#$it" } ?: ""  // 세 번째 해시태그

            // 해시태그가 없을 경우 TextView를 숨기기
            tvHashtag1.visibility = if (hashTags.size > 0) View.VISIBLE else View.GONE
            tvHashtag2.visibility = if (hashTags.size > 1) View.VISIBLE else View.GONE
            tvHashtag3.visibility = if (hashTags.size > 2) View.VISIBLE else View.GONE


        }

    }

    override fun getItemCount() = items.size

    private fun showOptionsBottomSheet(context: Context, item: DiaryMainCardData) {
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

    override fun onEditComplete(position: Int, editedItem: DiaryMainCardData) {
        if (position in 0 until items.size) {
            items[position] = editedItem
            notifyItemChanged(position)
        }
    }

    private fun showDeleteConfirmationDialog(context: Context, item: DiaryMainCardData) {
        val fragmentManager = (context as FragmentActivity).supportFragmentManager
        val dialogFragment = DiaryDeleteDialogFragment()

        dialogFragment.onDeleteConfirmed = {
            deleteMemory(item)
        }
        dialogFragment.show(fragmentManager, DiaryDeleteDialogFragment.TAG)
    }


    private fun playAudio() {
        if (audioUrl == null) {
            Log.e("PicAssociationFragment", "Audio URL is null")
            return
        }

        if(mediaPlayer==null){
            mediaPlayer=MediaPlayer().apply {
                setOnPreparedListener {
                    start()
                    this@CardViewAdapter.isPlaying=true
                }
                setOnCompletionListener {
                    resetAudio()
                    this@CardViewAdapter.isPlaying=false
                }
                try {
                    setDataSource(audioUrl)
                    prepareAsync()
                }catch (e:IOException){
                    Log.e("PicAssociationFragment", "Error playing audio", e)
                }
            }

        }else{
            if(isPlaying){
                mediaPlayer?.pause()
                isPlaying=false
            }else{
                mediaPlayer?.start()
            }
        }
    }

    private fun resetAudio() {
        mediaPlayer?.release()
        mediaPlayer=null
        isPlaying=false
    }

    private fun deleteMemory(item: DiaryMainCardData) {
        val position = items.indexOf(item)
        if (position != -1) {
            items.removeAt(position)
            notifyItemRemoved(position)
            onItemDeleted(item.id.toLong(), item)  // 삭제 시 콜백 호출
            // 여기에 데이터베이스나 서버에서 실제로 데이터를 삭제하는 로직을 추가할 수 있습니다.
        }
    }
}