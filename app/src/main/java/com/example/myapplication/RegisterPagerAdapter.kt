package com.example.myapplication

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import com.example.myapplication.Onboarding.OnboardingFirstFragment
import com.example.myapplication.Onboarding.OnboardingFourthFragment
import com.example.myapplication.Onboarding.OnboardingLoginFragment
import com.example.myapplication.Onboarding.OnboardingSecondFragment
import com.example.myapplication.Onboarding.OnboardingThirdFragment

class RegisterPagerAdapter(fm: FragmentManager) : FragmentPagerAdapter(fm,
    BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT) {
    override fun getItem(position: Int): Fragment {
        return when(position){
            0 -> OnboardingFirstFragment()
            1 -> OnboardingSecondFragment()
            2 -> OnboardingThirdFragment()
            3 -> OnboardingFourthFragment()
            else -> OnboardingLoginFragment()
        }
    }
    override fun getCount() =  5
}