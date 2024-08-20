package com.example.myapplication.Login

import android.content.Context
import android.graphics.Color
import android.graphics.Typeface
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.myapplication.R
import com.example.myapplication.databinding.ItemRoleBinding
import com.example.myapplication.databinding.ItemRoleEditBinding

class RoleAdapter(
    private val context: Context,
    private val roles: MutableList<String>,
    private val onItemSelected: (String) -> Unit
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private val VIEW_TYPE_EDIT = 0
    private val VIEW_TYPE_ITEM = 1
    private var selectedPosition: Int = -1

    inner class EditViewHolder(val binding: ItemRoleEditBinding) : RecyclerView.ViewHolder(binding.root)
    inner class ItemViewHolder(val binding: ItemRoleBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            VIEW_TYPE_EDIT -> {
                val binding = ItemRoleEditBinding.inflate(LayoutInflater.from(parent.context), parent, false)
                EditViewHolder(binding)
            }
            else -> {
                val binding = ItemRoleBinding.inflate(LayoutInflater.from(parent.context), parent, false)
                ItemViewHolder(binding)
            }
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is EditViewHolder -> {
                holder.binding.editTextRole.apply {
                    hint = "직접 작성할게요"
                    setHintTextColor(Color.GRAY)
                    setTextColor(Color.BLACK)
                    /*setPadding(20, 20, 0, 20)*/

                    setOnEditorActionListener { v, actionId, event ->
                        if (actionId == EditorInfo.IME_ACTION_DONE) {
                            val newRole = v.text.toString().trim()
                            if (newRole.isNotEmpty() && !roles.contains(newRole)) {
                                roles.add(1, newRole)
                                selectedPosition = 1  // Ensure the new role is selected
                                notifyDataSetChanged()
                                setText("")
                                onItemSelected(newRole)
                            }
                            true
                        } else {
                            false
                        }
                    }
                }
            }
            is ItemViewHolder -> {
                val role = roles[holder.adapterPosition]
                holder.binding.textViewRole.apply {
                    text = role
                    setTextColor(if (holder.adapterPosition == selectedPosition) Color.BLACK else Color.parseColor("#484848"))
                    typeface = if (holder.adapterPosition == selectedPosition)
                        Typeface.create("Pretendard700", Typeface.BOLD)
                    else
                        Typeface.create("Pretendard400", Typeface.NORMAL)
                    /*setPadding(20, 10, 0, 10)*/
                    background = if (holder.adapterPosition == selectedPosition) {
                        ContextCompat.getDrawable(context, R.drawable.ic_login_selected)
                    } else {
                        null
                    }
                    setOnClickListener {
                        selectedPosition = holder.adapterPosition
                        notifyDataSetChanged()
                        onItemSelected(role)
                    }
                }
            }
        }
    }

    override fun getItemCount() = roles.size

    override fun getItemViewType(position: Int): Int {
        return if (position == 0) VIEW_TYPE_EDIT else VIEW_TYPE_ITEM
    }
}
