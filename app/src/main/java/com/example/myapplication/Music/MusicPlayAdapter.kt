package com.example.myapplication.Music

import android.content.Context
import android.media.MediaPlayer
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.myapplication.Data.Request.MusicBookmark
import com.example.myapplication.Data.Request.MusicDelete
import com.example.myapplication.Data.Response.MusicBookmarkResponse
import com.example.myapplication.Data.Response.MusicDeleteResponse
import com.example.myapplication.R
import com.example.myapplication.Retrofit.PlaylistIF
import com.example.myapplication.Retrofit.RetrofitService
import com.example.myapplication.databinding.AlbumMusicItemBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.Collections

class MusicPlayAdapter(
    private val context: Context,
    private val playlistId:Long,
    private val items: ArrayList<MusicAlbumData>,
    private val dragRecyclerview: RecyclerView,
    private val itemClickListener: OnItemClickListener,
) : RecyclerView.Adapter<MusicPlayAdapter.ViewHolder>() {

    interface OnItemClickListener {
        fun onItemClick(position: Int)
    }

    private var itemDragHelper: ItemTouchHelper? = null

    private val helperCallback = DragHelperCallback { fromPos, toPos ->
        Collections.swap(items, fromPos, toPos)
        notifyItemMoved(fromPos, toPos)
        Log.d("시작: $fromPos", "변경: $toPos")
        Log.d("items", "$items")
        true
    }

    init {
        dragRecyclerview.adapter = this
        itemDragHelper = ItemTouchHelper(helperCallback).apply {
            attachToRecyclerView(dragRecyclerview)
        }
    }

    inner class ViewHolder(val binding: AlbumMusicItemBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(item: MusicAlbumData) {
            Glide.with(binding.ivItemCover.context)
                .load(item.imageUrl)
                .into(binding.ivItemCover)
            binding.tvItemTitle.text = item.musicTitle
            binding.tvItemTag1.text = "#"+item.musicTag1+" "
            binding.tvItemTag2.text = "#"+item.musicTag2

            binding.ivItemHeartEmpty.setOnClickListener {
                toggleHeart(memoryId=item.memoryId)
            }

            binding.apply {
                val clickListener=View.OnClickListener {
                    itemClickListener.onItemClick(adapterPosition)
                }
                ivItemCover.setOnClickListener(clickListener)
                tvItemTag1.setOnClickListener(clickListener)
                tvItemTag2.setOnClickListener(clickListener)
                tvItemTitle.setOnClickListener(clickListener)
            }

            binding.ivItemOption.setOnClickListener {
                showDeleteConfirmationDialog(itemView.context, item)
            }

            Log.e("MusicPlayAdapter","in fun bind")

        }

        private fun toggleHeart(memoryId: Int) {
            if (binding.ivItemHeartEmpty.tag == null || binding.ivItemHeartEmpty.tag == "empty") {
                binding.ivItemHeartEmpty.setImageResource(R.drawable.ic_music_heart)
                binding.ivItemHeartEmpty.tag = "filled"
            } else {
                binding.ivItemHeartEmpty.setImageResource(R.drawable.icon_heart_gray_empty)
                binding.ivItemHeartEmpty.tag = "empty"
            }
            apiBookmark(memoryId)
        }
    }

    fun apiBookmark(memoryId: Int){
        val apiService = RetrofitService.createRetrofit(context).create(PlaylistIF::class.java)
        val musicBookmark= MusicBookmark(memoryId)
        val request=apiService.sendMusicBookmark(musicBookmark)
        request.enqueue(object : Callback<MusicBookmarkResponse> {
            override fun onResponse(
                call: retrofit2.Call<MusicBookmarkResponse>,
                response: Response<MusicBookmarkResponse>
            ) {
                if(response.isSuccessful){
                    val bookmarkResponse = response.body()
                    Log.d("MusicAlbumAdapter",bookmarkResponse.toString())
                }else{
                    Log.e("apiBookmark", "Request failed with code: ${response.code()}")
                }
            }

            override fun onFailure(call: retrofit2.Call<MusicBookmarkResponse>, t: Throwable) {
                Log.e("apiBookmark", "Network error: ${t.message}")
            }
        })
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = AlbumMusicItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(items[position])
    }

    override fun getItemCount(): Int {
        return items.size
    }

    inner class DragHelperCallback(
        private val actionDrag: (Int, Int) -> Boolean
    ) : ItemTouchHelper.Callback() {
        override fun getMovementFlags(
            recyclerView: RecyclerView,
            viewHolder: RecyclerView.ViewHolder
        ): Int {
            val dragFlag = ItemTouchHelper.UP or ItemTouchHelper.DOWN
            return makeMovementFlags(dragFlag, 0)
        }

        override fun onMove(
            recyclerView: RecyclerView,
            viewHolder: RecyclerView.ViewHolder,
            target: RecyclerView.ViewHolder
        ): Boolean {
            return actionDrag.invoke(viewHolder.adapterPosition, target.adapterPosition)
        }

        override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {}

        // 꾹 누르면 드래그가 시작되도록 설정
        override fun isLongPressDragEnabled(): Boolean = true
    }
    private fun showDeleteConfirmationDialog(context: Context, item: MusicAlbumData) {
        Log.d("MusicAlbumAdapter", "Showing delete dialog for item: ${item.musicTitle}")
        val fragmentManager = (context as FragmentActivity).supportFragmentManager
        val dialogFragment = MusicDeleteDialogFragment()

        dialogFragment.onDeleteConfirmed = {
            deleteMemory(item)
            apiDelete(playlistId = playlistId, memoryId = item.memoryId)
        }

        dialogFragment.show(fragmentManager, MusicDeleteDialogFragment.TAG)
    }

    private fun deleteMemory(item: MusicAlbumData) {
        val position = items.indexOf(item)
        if (position != -1) {
            items.removeAt(position)
            notifyItemRemoved(position)
        }
    }

    private fun apiDelete(playlistId: Long, memoryId: Int) {

        val apiService = RetrofitService.createRetrofit(context).create(PlaylistIF::class.java)
        val musicDelete= MusicDelete(playlistId,memoryId)
        val call = apiService.sendMusicDelete(musicDelete)

        call.enqueue(object : Callback<MusicDeleteResponse> {
            override fun onResponse(
                call: Call<MusicDeleteResponse>,
                response: Response<MusicDeleteResponse>
            ) {
                if (response.isSuccessful) {
                    val responseBody = response.body()
                    responseBody?.let {
                        if (it.isSuccess) {
                            // 요청 성공에 따른 로직 처리
                            Log.e("API_DELETE", "삭제 성공: ${it.message}")
                        } else {
                            // 서버 응답이 실패로 처리될 경우
                            Log.e("API_DELETE", "서버 오류: ${it.message}")
                        }
                    }
                } else {
                    Log.e("API_DELETE", "응답 실패: ${response.message()}")
                }
            }

            override fun onFailure(call: Call<MusicDeleteResponse>, t: Throwable) {
                Log.e("API_DELETE", "네트워크 오류: ${t.message}")
            }
        })
    }


}