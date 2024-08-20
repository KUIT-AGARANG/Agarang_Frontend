package com.example.myapplication.Diary

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import com.example.myapplication.databinding.FragmentDiaryDialogBinding

class DiaryDeleteDialogFragment : DialogFragment() {
    private var _binding: FragmentDiaryDialogBinding? = null
    private val binding get() = _binding!!

    var onDeleteConfirmed: (() -> Unit)? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentDiaryDialogBinding.inflate(inflater, container, false)

        dialog?.window?.setBackgroundDrawableResource(android.R.color.transparent)

        binding.ivDiaryDialogNo.setOnClickListener {
            dismiss()
        }

        binding.ivDiaryDialogYes.setOnClickListener {
            onDeleteConfirmed?.invoke()
            dismiss()
        }

        return binding.root
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        const val TAG = "DiaryDeleteDialogFragment"
    }
}