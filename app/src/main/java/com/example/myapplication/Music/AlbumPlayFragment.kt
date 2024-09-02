package com.example.myapplication.Music

import android.content.Context
import android.media.MediaPlayer
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SeekBar
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.example.myapplication.Data.SharedViewModel
import com.example.myapplication.databinding.FragmentAlbumPlayBinding

class AlbumPlayFragment : Fragment() {
    private lateinit var binding: FragmentAlbumPlayBinding
    private var itemList: ArrayList<MusicAlbumData> = arrayListOf()
    private var musicAlbumPlayAdapter: MusicPlayAdapter? = null
    private var currentTrack: MusicAlbumData? = null
    private val handler = Handler(Looper.getMainLooper())
    private var updateSeekBarRunnable: Runnable? = null
    private var mediaPlayer: MediaPlayer? = null

    private lateinit var sharedViewModel: SharedViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentAlbumPlayBinding.inflate(inflater, container, false)
        val musicAlbumData: MusicAlbumData? = arguments?.getParcelable("music_album_data")
        val playlist: ArrayList<MusicAlbumData>? = arguments?.getParcelableArrayList("play_list")

        setupUIListeners()

        sharedViewModel = ViewModelProvider(requireActivity()).get(SharedViewModel::class.java)

        // 음악 재생
        musicAlbumData?.let {
            currentTrack = it  // currentTrack을 초기화
            playTrack(it)
        }

        playlist?.let {
            itemList.addAll(it)
        }

        val playlistId = arguments?.getLong("playlistId")
        musicAlbumPlayAdapter = MusicPlayAdapter(
            requireContext(),
            playlistId = playlistId!!,
            itemList,
            binding.rvMusicAlbum,
            object : MusicPlayAdapter.OnItemClickListener {
                override fun onItemClick(position: Int) {
                    val item = itemList[position]
                    currentTrack = item  // 현재 선택한 트랙을 currentTrack으로 설정
                    playTrack(item)
                }
            }
        )
        binding.rvMusicAlbum.adapter = musicAlbumPlayAdapter
        binding.rvMusicAlbum.layoutManager = LinearLayoutManager(requireContext())

        setupSeekBarUpdater()
        return binding.root
    }
    
    //기존 버전
    /*private fun playTrack(track: MusicAlbumData) {
        Log.d("AlbumPlayFragment", "Playing track: ${track.musicTitle}")
        // 기존 MediaPlayer 해제 및 초기화
        mediaPlayer?.release()
        mediaPlayer = MediaPlayer().apply {
            setDataSource(track.musicUrl)
            prepareAsync()
            setOnPreparedListener {
                start()
                updateSeekBar()
                sharedViewModel.setCurrentTrack(track) // ViewModel에 현재 트랙 업데이트
                saveLastPlayedTrack(track) // SharedPreferences에 저장
                togglePlayPause(true)
                updateUIWithTrackInfo(track)
            }
            setOnCompletionListener {
                togglePlayPause(false)
                playNextTrack(track) // 트랙이 끝나면 자동으로 다음 트랙 재생
            }
        }
    }*/
    
    //음악이 생성중일때 널값이 오면 음악이 생성중이라고 toast 메세지를 띄움
    private fun playTrack(track: MusicAlbumData) {
        Log.d("AlbumPlayFragment", "Playing track: ${track.musicTitle}")

        if (track.musicUrl.isEmpty()) {
            Toast.makeText(context, "음악이 생성 중입니다. 잠시만 기다려 주세요.", Toast.LENGTH_SHORT).show()
            return
        }

        // 기존 MediaPlayer 해제 및 초기화
        mediaPlayer?.release()
        mediaPlayer = MediaPlayer().apply {
            setDataSource(track.musicUrl)
            prepareAsync()
            setOnPreparedListener {
                start()
                updateSeekBar()
                sharedViewModel.setCurrentTrack(track) // ViewModel에 현재 트랙 업데이트
                saveLastPlayedTrack(track) // SharedPreferences에 저장
                togglePlayPause(true)
                updateUIWithTrackInfo(track)
            }
            setOnCompletionListener {
                togglePlayPause(false)
                playNextTrack(track) // 트랙이 끝나면 자동으로 다음 트랙 재생
            }
        }
    }

    private fun updateSeekBar() {
        val updateSeekBarRunnable = object : Runnable {
            override fun run() {
                val currentPosition = mediaPlayer?.currentPosition ?: 0
                val duration = mediaPlayer?.duration ?: 0

                binding.seekBar.progress = currentPosition
                binding.seekBar.max = duration

                // 시간을 포맷팅하여 표시 (00:00 형식)
                binding.tvMusicTime1.text = formatTime(currentPosition)
                binding.tvMusicTime2.text = formatTime(duration)

                handler.postDelayed(this, 1000) // 1초마다 업데이트
            }
        }
        handler.post(updateSeekBarRunnable)
    }

    private fun setupUIListeners() {

        binding.ivPlayStartIc.setOnClickListener {
            mediaPlayer?.start()
            togglePlayPause(true)
        }

        binding.ivPlayStopIc.setOnClickListener {
            mediaPlayer?.pause()
            togglePlayPause(false)
        }

        binding.seekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                if (fromUser) {
                    mediaPlayer?.seekTo(progress)
                }
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {}
            override fun onStopTrackingTouch(seekBar: SeekBar?) {}
        })

        // 다음 곡, 이전 곡 버튼 설정
        binding.ivPlayBackIc.setOnClickListener {
            currentTrack?.let {
                val prevTrack = playPreviousTrack(it)
                Log.d("AlbumPlayFragment", "Previous track: $prevTrack")
                prevTrack?.let { track ->
                    currentTrack = track
                    playTrack(track)
                }
            }
        }

        binding.ivPlayForeIc.setOnClickListener {
            currentTrack?.let {
                val nextTrack = playNextTrack(it)
                Log.d("AlbumPlayFragment", "Next track: $nextTrack")
                nextTrack?.let { track ->
                    currentTrack = track
                    playTrack(track)
                }
            }
        }
    }


    private fun togglePlayPause(isPlaying: Boolean) {
        binding.ivPlayStopIc.isVisible = isPlaying
        binding.ivPlayStartIc.isVisible = !isPlaying
    }

    private fun updateUIWithTrackInfo(track: MusicAlbumData) {
        Glide.with(binding.ivAlbumCover.context)
            .load(track.imageUrl)
            .into(binding.ivAlbumCover)
        binding.tvPlayMusicName.text = track.musicTitle
        binding.tvPlayMusicHashTag.text = "#${track.musicTag1}"
        binding.tvPlayMusicHashTag2.text = "#${track.musicTag2}"
    }

    private fun saveLastPlayedTrack(track: MusicAlbumData) {
        val sharedPreferences = context?.getSharedPreferences("MusicPrefs", Context.MODE_PRIVATE)
        sharedPreferences?.edit()?.apply {
            putString("LAST_PLAYED_TITLE", track.musicTitle)
            putString("LAST_PLAYED_URL", track.musicUrl)
            putString("LAST_PLAYED_IMAGE", track.imageUrl)
            putString("LAST_PLAYED_HASH_TAG1", track.musicTag1)
            putString("LAST_PLAYED_HASH_TAG2", track.musicTag2)
            apply()
        }
    }

    private fun setupSeekBarUpdater() {
        updateSeekBarRunnable = object : Runnable {
            override fun run() {
                mediaPlayer?.let {
                    binding.seekBar.progress = it.currentPosition
                    binding.seekBar.max = it.duration
                    binding.tvMusicTime1.text = formatTime(it.currentPosition)
                    binding.tvMusicTime2.text = formatTime(it.duration)
                }
                handler.postDelayed(this, 1000)
            }
        }
        handler.post(updateSeekBarRunnable!!)
    }

    private fun formatTime(milliseconds: Int): String {
        val minutes = (milliseconds / 1000) / 60
        val seconds = (milliseconds / 1000) % 60
        return String.format("%02d:%02d", minutes, seconds)
    }

    private fun playPreviousTrack(currentTrack: MusicAlbumData): MusicAlbumData? {
        val currentIndex = itemList.indexOf(currentTrack)
        return if (currentIndex > 0) {
            itemList[currentIndex - 1]
        } else {
            null
        }
    }

    private fun playNextTrack(currentTrack: MusicAlbumData): MusicAlbumData? {
        val currentIndex = itemList.indexOf(currentTrack)
        return if (currentIndex < itemList.size - 1) {
            itemList[currentIndex + 1]
        } else {
            null
        }
    }

    private fun getFirstTrack(): MusicAlbumData? {
        return if (itemList.isNotEmpty()) itemList[0] else null
    }

    private fun getLastTrack(): MusicAlbumData? {
        return if (itemList.isNotEmpty()) itemList[itemList.size - 1] else null
    }

    override fun onDestroyView() {
        super.onDestroyView()
        updateSeekBarRunnable?.let { handler.removeCallbacks(it) }
        mediaPlayer?.release()
        mediaPlayer = null
    }
}
