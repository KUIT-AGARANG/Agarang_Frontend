package com.example.myapplication.Music

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.myapplication.R
import com.example.myapplication.databinding.FragmentMusicAlbumBinding
import com.example.myapplication.databinding.FragmentMusicBinding


class MusicAlbumFragment : Fragment() {
    lateinit var binding: FragmentMusicAlbumBinding
    private var itemList:ArrayList<MusicAlbumData> = arrayListOf()
    private var musicAlbumAdapter:MusicAlbumAdapter?=null


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding=FragmentMusicAlbumBinding.inflate(inflater,container,false)

        itemList.add(MusicAlbumData(R.drawable.music_image_sample,"즐겨 찾기", "신남","신남"))
        itemList.add(MusicAlbumData(R.drawable.music_image_sample,"운동할 때 들을 플레이리스트","즐거움", "즐거움"))
        itemList.add(MusicAlbumData(R.drawable.music_image_sample,"플레이리스트", "산책","산책"))
        itemList.add(MusicAlbumData(R.drawable.music_image_sample,"즐겨 찾기", "신남","신남"))
        itemList.add(MusicAlbumData(R.drawable.music_image_sample,"운동할 때 들을 플레이리스트","즐거움", "즐거움"))
        itemList.add(MusicAlbumData(R.drawable.music_image_sample,"플레이리스트", "산책","산책"))
        itemList.add(MusicAlbumData(R.drawable.music_image_sample,"즐겨 찾기", "신남","신남"))
        itemList.add(MusicAlbumData(R.drawable.music_image_sample,"운동할 때 들을 플레이리스트","즐거움", "즐거움"))
        itemList.add(MusicAlbumData(R.drawable.music_image_sample,"플레이리스트", "산책","산책"))

        recycler()

        return binding.root
    }
    private fun recycler() {
        musicAlbumAdapter=MusicAlbumAdapter(itemList,requireActivity(), object :MusicAlbumAdapter.OnItemClickListener{
            override fun onItemClick(position: Int) {
                val item=itemList[position]
                val bundle=Bundle().apply {
                    putParcelable("music_album_data", item)
                    putParcelableArrayList("play_list", itemList)
                }
                val fragment=AlbumPlayFragment().apply {
                    arguments=bundle
                }
                parentFragmentManager.beginTransaction()
                    .replace(R.id.container,fragment)
                    .addToBackStack(null)
                    .commit()
            }
        })
        binding.rvMusicAlbum.adapter=musicAlbumAdapter
        binding.rvMusicAlbum.layoutManager = LinearLayoutManager(requireContext())
    }

}