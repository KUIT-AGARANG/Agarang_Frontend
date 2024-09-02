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

class MoodAdapter(
    private val context: Context,
    private val items: Array<String>,
    private val fragmentManager: FragmentManager,
    private val sharedViewModel: SharedViewModel,
    private val questionId: String
) : BaseAdapter() {

    val moodMapping = mapOf(
        "아름다운" to "BEAUTIFUL",
        "밝은" to "BRIGHT",
        "행복한" to "HAPPY",
        "평화로운" to "PEACEFUL",
        "따뜻한" to "WARM",
        "활기찬" to "ENERGETIC",
        "기쁜" to "FANTASTIC",
        "환상적인" to "LOVELY",
        "사랑스러운" to "TOUCHING")

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
            view = inflater.inflate(R.layout.mood_item, parent, false)
        }

        val moodOption = view?.findViewById<TextView>(R.id.mood_option)
        val backgroundSelected = view?.findViewById<ImageView>(R.id.background_selected)

        // 아이템의 텍스트 설정
        moodOption?.text = items[position].toString()

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
            val selectedMood = items[position]
            val moodCode = moodMapping[selectedMood] ?: selectedMood
            Log.e("MoodAdapter",moodCode)
            sharedViewModel.setMood(moodCode)

            // SelectSpeedFragment로 전환
            val transaction: FragmentTransaction = fragmentManager.beginTransaction()
            val fragment=SelectSpeedFragment()
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