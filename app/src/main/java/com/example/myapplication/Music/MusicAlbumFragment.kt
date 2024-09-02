package com.example.myapplication.Music

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.example.myapplication.Data.Response.TrackResponse
import com.example.myapplication.R
import com.example.myapplication.Retrofit.PlaylistIF
import com.example.myapplication.Retrofit.RetrofitService
import com.example.myapplication.databinding.FragmentMusicAlbumBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MusicAlbumFragment : Fragment() {
    private lateinit var binding: FragmentMusicAlbumBinding
    private val itemList: ArrayList<MusicAlbumData> = arrayListOf()
    private var musicAlbumAdapter: MusicAlbumAdapter? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentMusicAlbumBinding.inflate(inflater, container, false)
        Log.e("MusicAlbumFragment","one")

        val playlistId=arguments?.getLong("playlistId")
        val playlistImg=arguments?.getString("playlistPicture")
        val playlistName=arguments?.getString("playlistName")
        playlistId?.let { apiService(it) }
        Glide.with(binding.ivAlbumCover.context)
            .load(playlistImg)
            .into(binding.ivAlbumCover)
        binding.tvAlbumTitle.text = playlistName ?: "Unknown"

        if (playlistId != null) {
            setupRecyclerView(playlistId)
        }

        return binding.root
    }

    private fun apiService(playlistId:Long) {
        val apiService = RetrofitService.createRetrofit(requireContext()).create(PlaylistIF::class.java)
        val response=apiService.getTracklist(playlistId)
        response.enqueue(object :Callback<TrackResponse>{
            override fun onResponse(call: Call<TrackResponse>, response: Response<TrackResponse>) {
                if(response.isSuccessful){
                    val result = response.body()?.result
                    if (result != null) {
                        val tracks = result.tracks
                        binding.tvMusicTotal.text = "총 "+result.totalTrackCount.toString()+"곡"
                        binding.tvTotalTime.text = result.totalTrackTime.toString()+"초"
                        tracks?.let {
                            for(tracks in it){
                                val tag1 = if (tracks.hashTags.isNotEmpty()) tracks.hashTags[0] else ""
                                val tag2 = if (tracks.hashTags.size > 1) tracks.hashTags[1] else ""
                                itemList.add(
                                    MusicAlbumData(
                                        memoryId=tracks.memoryId,
                                        imageUrl = tracks.imageUrl,
                                        musicTitle = tracks.musicTitle,
                                        musicUrl = tracks.musicUrl ?: "",
                                        musicTag1 = tag1,
                                        musicTag2 = tag2,
                                        bookmarked = tracks.bookmarked
                                    )
                                )
                            }
                        }
                        musicAlbumAdapter?.notifyDataSetChanged()
                    }
                }else{
                    Log.e("MusicAlbumFragment","response is not successful")
                }
            }

            override fun onFailure(call: Call<TrackResponse>, t: Throwable) {
                Log.e("MusicAlbumFragment","network error")
            }

        })
    }

    private fun setupRecyclerView(playlistId:Long) {
        musicAlbumAdapter = MusicAlbumAdapter(requireContext(),playlistId,itemList, object : MusicAlbumAdapter.OnItemClickListener {
            override fun onItemClick(position: Int) {
                val item = itemList[position]
                val bundle = Bundle().apply {
                    putParcelable("music_album_data", item)
                    putParcelableArrayList("play_list", itemList)
                    putLong("playlistId",playlistId)
                }
                val fragment = AlbumPlayFragment().apply {
                    arguments = bundle
                }
                parentFragmentManager.beginTransaction()
                    .replace(R.id.main_frm, fragment)
                    .addToBackStack(null)
                    .commit()
            }
        })

        binding.rvMusicAlbum.apply {
            adapter = musicAlbumAdapter
            layoutManager = LinearLayoutManager(requireContext())
        }
    }

}
