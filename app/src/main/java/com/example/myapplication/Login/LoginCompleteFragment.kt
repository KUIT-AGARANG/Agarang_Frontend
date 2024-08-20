package com.example.myapplication.Login

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.myapplication.MainActivity
import com.example.myapplication.databinding.FragmentLoginCompleteBinding


class LoginCompleteFragment : Fragment() {

    lateinit var binding: FragmentLoginCompleteBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentLoginCompleteBinding.inflate(inflater, container, false)

        binding.ivLoginCompleteExit.setOnClickListener {
            val intent = Intent(requireContext(), MainActivity :: class.java)
            startActivity(intent)
        }

        return binding.root
    }


}