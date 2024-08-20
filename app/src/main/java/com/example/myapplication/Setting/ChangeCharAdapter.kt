package com.example.myapplication.Setting

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.ImageView
import androidx.fragment.app.FragmentActivity
import com.bumptech.glide.Glide
import com.example.myapplication.Data.Response.HomeChangeCharResponse
import com.example.myapplication.R

class ChangeCharAdapter(
    private val context: Context,
    private val items: List<HomeChangeCharResponse.Character>,

    // 캐릭터 변경을 처리하는 리스너
    private val changeListener: (HomeChangeCharResponse.Character)-> Unit
) : BaseAdapter() {

    private var selectedPosition: Int = -1

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
            view = inflater.inflate(R.layout.char_item, parent, false)
        }
//추가
        val backgroundSelected = view?.findViewById<ImageView>(R.id.background_selected)
        val checkOrange = view?.findViewById<ImageView>(R.id.check_orange)
        val checkGray = view?.findViewById<ImageView>(R.id.check_gray)
        val iconImageView = view?.findViewById<ImageView>(R.id.icon_image)


        val character = items[position]

        // 이미지 URL을 로드하여 ImageView에 설정 (예: Glide 사용)
        Glide.with(context)
            .load(character.imageUrl)
            .into(iconImageView!!)
// 추가. 선택된 아이템의 배경 표시
        backgroundSelected?.visibility = if (selectedPosition == position) View.VISIBLE else View.GONE

//체크표시 추가
        if (position == selectedPosition) {
            checkOrange?.visibility = View.VISIBLE
            checkGray?.visibility = View.GONE
        } else {
            checkOrange?.visibility = View.GONE
            checkGray?.visibility = View.VISIBLE
        }

        // 아이템 클릭 이벤트 처리
        iconImageView.setOnClickListener {
            val activity = context as FragmentActivity
            val dialogFragment = ItemDetailDialogFragment.newInstance(
                character.imageUrl, // URL 전달
                character.name,     // 이름 전달
                character.description // 설명 전달
            ).apply {
                /*setChangeListener(changeListener)*/
                setChangeListener { selectedCharacterUrl->
                    changeListener(character)
                }
            }
            dialogFragment.show(activity.supportFragmentManager, "ItemDetailDialogFragment")
        }

        return view ?: throw IllegalStateException("View should not be null")
    }
    //추가 함수
    fun setSelectedPosition(position: Int) {
        selectedPosition = position
        notifyDataSetChanged()
    }
}