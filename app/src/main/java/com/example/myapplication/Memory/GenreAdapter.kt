package com.example.myapplication.Memory

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import com.example.myapplication.R
import com.example.myapplication.Data.SharedViewModel

class GenreAdapter(
    private val context: Context,
    private val items: Array<String>,
    private val fragmentManager: FragmentManager,
    private val sharedViewModel: SharedViewModel,
    private val questionId: String
) : BaseAdapter() {

    val genreMapping = mapOf(
        "발라드" to "BALLAD",
        "팝" to "POP",
        "재즈" to "JAZZ",
        "어쿠스틱" to "ACOUSTIC",
        "알앤비" to "RNB",
        "일렉트로닉" to "ELECTRONIC",
        "락" to "ROCK",
        "인디고" to "INDIE",
        "힙합" to "HIPHOP"
    )

    // 선택된 아이템의 인덱스를 저장할 변수
    private var selectedPosition = -1

    override fun getCount(): Int {
        return items.size
    }

    override fun getItem(position: Int): Any {
        return items[position]
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        var view = convertView
        if (view == null) {
            val inflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
            view = inflater.inflate(R.layout.genre_item, parent, false)
        }

        val genreOption = view?.findViewById<TextView>(R.id.genre_option)
        val backgroundSelected = view?.findViewById<ImageView>(R.id.background_selected)

        // 아이템의 텍스트 설정
        genreOption?.text = items[position].toString()

        // 선택된 아이템이면 배경을 보이게, 아니면 감추기
        if (position == selectedPosition) {
            backgroundSelected?.visibility = View.VISIBLE
        } else {
            backgroundSelected?.visibility = View.GONE
        }

        // 아이템 클릭 이벤트 처리
        view?.setOnClickListener {
            selectedPosition = position
            notifyDataSetChanged()  // 뷰 갱신

            //한글 감정 -> 영어 대문자
            val selectGenre = items[position]
            val genreCode = genreMapping[selectGenre] ?: selectGenre
            Log.e("GenreAdapter",genreCode)
            sharedViewModel.setGenre(genreCode)

            // SelectMoodFragment로 전환
            val transaction: FragmentTransaction = fragmentManager.beginTransaction()
            val fragment=SelectMoodFragment()
            val bundle = Bundle()
            bundle.putString("id", questionId)
            fragment.arguments = bundle
            transaction.replace(R.id.memory_frm, fragment)
            transaction.addToBackStack(null)
            transaction.commit()
        }

        return view!!
    }
}