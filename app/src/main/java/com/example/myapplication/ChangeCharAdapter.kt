package com.example.myapplication

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.FragmentActivity

class ChangeCharAdapter(
    private val context: Context,
    private val items: IntArray,
    private val charnames: Array<String>,
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
        val view: View
        val holder: ViewHolder

        if (convertView == null) {
            view = LayoutInflater.from(context).inflate(R.layout.char_item, parent, false)
            holder = ViewHolder()
            holder.imageView = view.findViewById(R.id.icon_image)
            holder.textView = view.findViewById(R.id.tv_char_name)
            holder.textView = view.findViewById(R.id.description_text_view)
            view.tag = holder
        } else {
            view = convertView
            holder = view.tag as ViewHolder
        }

        holder.imageView.setImageResource(items[position])
        holder.textView.text = charnames[position]
        holder.textView.text = descriptions[position]

        view.setOnClickListener {
            val fragmentActivity = context as FragmentActivity
            val dialogFragment = ItemDetailDialogFragment.newInstance(items[position],charnames[position] ,descriptions[position])
            dialogFragment.show(fragmentActivity.supportFragmentManager, "item_detail_dialog")
        }

        return view
    }

    private class ViewHolder {
        lateinit var imageView: ImageView
        lateinit var textView: TextView
    }
}