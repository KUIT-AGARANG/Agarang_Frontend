package com.example.myapplication.Memory

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.myapplication.R
import com.example.myapplication.databinding.InstrumentItemBinding
import com.google.android.material.shape.ShapeAppearanceModel

class InstrumentAdapter(
    val instruments: List<InstrumentData>,
    private val onItemClickListener: OnItemClickListener
) : RecyclerView.Adapter<InstrumentAdapter.InstrumentViewHolder>() {

    private var selectedPosition: Int = -1

    interface OnItemClickListener {
        fun onItemClick(instrument: InstrumentData)
    }

    inner class InstrumentViewHolder(val binding: InstrumentItemBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(instrument: InstrumentData, position: Int) {
            binding.tvInstrument.text = instrument.instrumentName
            binding.ivInstrument.setImageResource(instrument.imageResId)

            if (selectedPosition == position) {
                binding.ivInstrumentBackground.setImageResource(R.drawable.ic_instrument_background_selected)
                binding.ivInstrument.setImageResource(R.drawable.ic_check)
            } else {
                binding.ivInstrumentBackground.setImageResource(R.drawable.ic_instrument_background)
                binding.ivInstrument.visibility = View.VISIBLE
            }

            val shapeAppearanceModel = if (instrument.shouldCrop) {
                ShapeAppearanceModel.builder()
                    .setAllCornerSizes(ShapeAppearanceModel.PILL)
                    .build()
            } else {
                ShapeAppearanceModel.builder().build()
            }
            binding.ivInstrument.shapeAppearanceModel = shapeAppearanceModel


//클릭리스너를 어댑터 말고 fragment 파일로 ㄱㄱ
            itemView.setOnClickListener {
                if (selectedPosition == position) {
                    selectedPosition = -1
                } else {
                    selectedPosition = position
                }
                notifyDataSetChanged()
                onItemClickListener.onItemClick(instrument)
            }

        }
    }

    override fun onBindViewHolder(holder: InstrumentViewHolder, position: Int) {
        holder.bind(instruments[position], position)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): InstrumentViewHolder {
        val binding = InstrumentItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return InstrumentViewHolder(binding)
    }


    override fun getItemCount() = instruments.size
}