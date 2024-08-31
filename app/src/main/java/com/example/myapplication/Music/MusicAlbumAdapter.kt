package com.example.myapplication.Music

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.FragmentActivity
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
import retrofit2.Callback
import retrofit2.Call
import retrofit2.Response

class MusicAlbumAdapter(
    private val context: Context,
    private val playlistId:Long,
    private val items: ArrayList<MusicAlbumData>,
    private val itemClickListener: OnItemClickListener,
) : RecyclerView.Adapter<MusicAlbumAdapter.ViewHolder>() {

    interface OnItemClickListener {
        fun onItemClick(position: Int)
    }

    inner class ViewHolder(private val binding: AlbumMusicItemBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(item: MusicAlbumData) {
            Glide.with(binding.ivItemCover.context)
                .load(item.imageUrl)
                .into(binding.ivItemCover)
            binding.tvItemTitle.text = item.musicTitle
            binding.tvItemTag1.text = "#"+item.musicTag1+" "
            binding.tvItemTag2.text = "#"+item.musicTag2


    // 초기 즐겨찾기 상태 설정
            if (item.bookmarked) {
                binding.ivItemHeartEmpty.setImageResource(R.drawable.ic_music_heart)
                binding.ivItemHeartEmpty.tag = "filled"
            } else {
                binding.ivItemHeartEmpty.setImageResource(R.drawable.icon_heart_gray_empty)
                binding.ivItemHeartEmpty.tag = "empty"
            }
    // 즐겨찾기 토글 기능
            binding.ivItemHeartEmpty.setOnClickListener {
                toggleHeart(item)
            }

            /*binding.ivItemHeartEmpty.setOnClickListener {
                toggleHeart(item.memoryId)
            }*/

            binding.apply {
                val clickListener= View.OnClickListener {
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

        }
//변경 전 파라메터: mempryId:Int, apiBookmark(memoryId)
        private fun toggleHeart(item: MusicAlbumData) {
            if (binding.ivItemHeartEmpty.tag == null || binding.ivItemHeartEmpty.tag == "empty") {
                binding.ivItemHeartEmpty.setImageResource(R.drawable.ic_music_heart)
                binding.ivItemHeartEmpty.tag = "filled"
                item.bookmarked = true // 즐겨찾기 상태 변경
            } else {
                binding.ivItemHeartEmpty.setImageResource(R.drawable.icon_heart_gray_empty)
                binding.ivItemHeartEmpty.tag = "empty"
                item.bookmarked = false // 즐겨찾기 상태 변경
            }
            //apiBookmark(memoryId)
            apiBookmark(item.memoryId)
        }
    }

    fun apiBookmark(memoryId: Int){
        val apiService = RetrofitService.createRetrofit(context).create(PlaylistIF::class.java)
        val musicBookmark=MusicBookmark(memoryId)
        val request=apiService.sendMusicBookmark(musicBookmark)
        request.enqueue(object : Callback<MusicBookmarkResponse>{
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
            Log.d("MusicAlbumAdapter", "Item deleted: ${item.musicTitle}")
        }
    }

    private fun apiDelete(playlistId: Long, memoryId: Int) {

        val apiService = RetrofitService.createRetrofit(context).create(PlaylistIF::class.java)
        val musicDelete=MusicDelete(playlistId,memoryId)
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