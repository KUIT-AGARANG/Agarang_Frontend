package com.example.myapplication.Music

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.example.myapplication.R
import com.example.myapplication.databinding.FragmentAlbumPlayBinding
import com.example.myapplication.databinding.FragmentMusicAlbumBinding

class AlbumPlayFragment : Fragment() {
    lateinit var binding:FragmentAlbumPlayBinding
    private var itemList: ArrayList<MusicAlbumData> = arrayListOf()
    private var musicAlbumPlayAdapter: MusicPlayAdapter? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding= FragmentAlbumPlayBinding.inflate(inflater,container,false)
        val musicAlbumData: MusicAlbumData? = arguments?.getParcelable("music_album_data")
        val playlist:ArrayList<MusicAlbumData>?=arguments?.getParcelableArrayList<MusicAlbumData>("play_list")
        musicAlbumData?.let {
            Glide.with(binding.ivAlbumCover.context)
                .load(it.imageUrl)
                .into(binding.ivAlbumCover)
            binding.tvPlayMusicName.text=it.musicTitle
            binding.tvPlayMusicHashTag.text=it.musicTag1
            binding.tvPlayMusicHashTag2.text=it.musicTag2
        }
        playlist?.let{
            itemList.addAll(it)
        }

        val playlistId=arguments?.getLong("playlistId")
        musicAlbumPlayAdapter = MusicPlayAdapter(requireContext(), playlistId = playlistId!!.toLong() ,itemList, binding.rvMusicAlbum, object :MusicPlayAdapter.OnItemClickListener{
            override fun onItemClick(position: Int) {
                val item=itemList[position]
                Glide.with(binding.ivAlbumCover.context)
                    .load(item.imageUrl)
                    .into(binding.ivAlbumCover)
                binding.tvPlayMusicName.text=item.musicTitle
                binding.tvPlayMusicHashTag.text=item.musicTag1
                binding.tvPlayMusicHashTag2.text=item.musicTag2
                //음악 실행되어야 함//
            }
        })
        binding.rvMusicAlbum.adapter = musicAlbumPlayAdapter
        binding.rvMusicAlbum.layoutManager = LinearLayoutManager(requireContext())

        return binding.root
    }

}