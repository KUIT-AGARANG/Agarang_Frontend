package com.example.myapplication.Music

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.myapplication.Data.Response.AllPlaylistResponse
import com.example.myapplication.R
import com.example.myapplication.Retrofit.PlaylistIF
import com.example.myapplication.Retrofit.RetrofitService
import com.example.myapplication.databinding.FragmentMusicBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MusicFragment : Fragment() {
    lateinit var binding:FragmentMusicBinding
    private var itemList:ArrayList<MusicMainData> = arrayListOf()
    private var musicMainAdapter:MusicMainAdapter?=null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding=FragmentMusicBinding.inflate(inflater,container,false)
        playlistService()
        recycler()

        return binding.root
    }

    private fun playlistService() {
        val playlistService = RetrofitService.createRetrofit(requireContext()).create(PlaylistIF::class.java)

        playlistService.getAllPlaylist().enqueue(object :Callback<AllPlaylistResponse>{
            override fun onResponse(
                call: Call<AllPlaylistResponse>,
                response: Response<AllPlaylistResponse>
            ) {
                if(response.isSuccessful){
                    val allPlaylistResponse=response.body()
                    val playlists=allPlaylistResponse?.result?.playlists
                    playlists?.let {
                        for (playlist in it){
                            itemList.add(
                                MusicMainData(
                                    playlistId = playlist.id.toLong(),
                                    musicImgUrl =playlist.imageUrl,
                                    musicContent=playlist.name
                                )
                            )
                        }
                    }
                    musicMainAdapter?.notifyDataSetChanged()
                }else{
                    Log.e("MusicFragment","response is not Succeessful")
                }
            }

            override fun onFailure(call: Call<AllPlaylistResponse>, t: Throwable) {
                Log.e("MusicFragment","network error")
            }

        })
    }

    private fun recycler() {
        musicMainAdapter=MusicMainAdapter(itemList,requireActivity())
        binding.rvMusic.adapter=musicMainAdapter
        val spanCount=2
        binding.rvMusic.layoutManager=GridLayoutManager(requireContext(),spanCount)
    }

}