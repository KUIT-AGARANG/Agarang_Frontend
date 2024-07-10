package com.example.myapplication

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.FragmentActivity
import com.example.myapplication.databinding.CharItemBinding

class ChangeCharAdapter(
    private val context: Context,
    private val items: IntArray,
    private val names: Array<String>,
    private val descriptions: Array<String>
) : BaseAdapter() {

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

        val iconImageView = view?.findViewById<ImageView>(R.id.icon_image)
        iconImageView?.setImageResource(items[position])

// 아이템 클릭 이벤트 처리
        iconImageView?.setOnClickListener {
            val activity = context as FragmentActivity
            val dialogFragment = ItemDetailDialogFragment.newInstance(
                items[position],
                names[position],
                descriptions[position]
            )
            dialogFragment.show(activity.supportFragmentManager, "ItemDetailDialogFragment")
        }
        return view ?: throw IllegalStateException("View should not be null")

    }
}