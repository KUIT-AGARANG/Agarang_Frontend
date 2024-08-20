package com.example.myapplication.Setting

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.myapplication.Data.Response.FamilyMember
import com.example.myapplication.R

class FamilyAdapter : RecyclerView.Adapter<FamilyAdapter.ViewHolder>() {
    private var familyMembers: List<FamilyMember> = emptyList()

    fun updateMembers(newMembers: List<FamilyMember>) {
        familyMembers = newMembers
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_family, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val member = familyMembers[position]
        holder.bind(member)
    }

    override fun getItemCount() = familyMembers.size

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        private val memberName: TextView = view.findViewById(R.id.tv_family_member)

        fun bind(member: FamilyMember) {
            memberName.text = "${member.name}"
        }
    }
}