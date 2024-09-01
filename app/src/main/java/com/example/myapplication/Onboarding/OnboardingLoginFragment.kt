package com.example.myapplication.Onboarding

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.myapplication.Login.LoginActivity
import com.example.myapplication.databinding.FragmentOnboardingLoginBinding


class OnboardingLoginFragment : Fragment() {

    lateinit var binding: FragmentOnboardingLoginBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentOnboardingLoginBinding.inflate(inflater, container, false)

        binding.ivStartButton.setOnClickListener{
            val intent = Intent(activity, LoginActivity::class.java)
            startActivity(intent)
        }

        return binding.root
    }

}