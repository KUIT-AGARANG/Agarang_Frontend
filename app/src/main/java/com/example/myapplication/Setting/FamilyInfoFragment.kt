package com.example.myapplication.Setting

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.myapplication.Data.Response.FamilyResponse
import com.example.myapplication.Data.Response.FamilyResult
import com.example.myapplication.R
import com.example.myapplication.Retrofit.HomeIF
import com.example.myapplication.Retrofit.LoginIF
import com.example.myapplication.Retrofit.RetrofitService
import com.example.myapplication.databinding.FragmentFamilyInfoBinding
import com.example.myapplication.databinding.FragmentHomeSettingBinding
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class FamilyInfoFragment : Fragment() {
    private lateinit var binding: FragmentFamilyInfoBinding
    private lateinit var familyAdapter: FamilyAdapter
    private lateinit var loginService: HomeIF

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentFamilyInfoBinding.inflate(inflater, container, false)
        setupRecyclerView()
        setupRetrofit()
        fetchFamilyInfo()
        return binding.root
    }

    private fun setupRecyclerView() {
        familyAdapter = FamilyAdapter()
        binding.rvFamilyInfo.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = familyAdapter
        }
    }

    private fun setupRetrofit() {
        loginService = RetrofitService.createRetrofit(requireContext()).create(HomeIF::class.java)
    }

    private fun fetchFamilyInfo() {
        loginService.getFamilyInfo().enqueue(object : Callback<FamilyResponse> {
            override fun onResponse(call: Call<FamilyResponse>, response: Response<FamilyResponse>) {
                if (response.isSuccessful) {
                    val familyResponse = response.body()
                    familyResponse?.let {
                        if (it.isSuccess) {
                            updateUI(it.result)
                        } else {
                            // Handle API error
                            Toast.makeText(context, it.message, Toast.LENGTH_SHORT).show()
                        }
                    }
                } else {
                    // Handle HTTP error
                    Toast.makeText(context, "서버 오류가 발생했습니다.", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<FamilyResponse>, t: Throwable) {
                // Handle network error
                Toast.makeText(context, "네트워크 오류가 발생했습니다.", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun updateUI(result: FamilyResult) {
        binding.tvFamilySharingCode.text = result.babyCode
        familyAdapter.updateMembers(result.members)
    }
}