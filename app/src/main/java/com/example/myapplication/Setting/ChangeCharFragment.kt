package com.example.myapplication.Setting

import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.GridView
import android.widget.ImageView
import android.widget.Toast
import androidx.fragment.app.FragmentTransaction
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.myapplication.Data.Request.CharacterUpdateRequest
import com.example.myapplication.Data.Response.HomeChangeCharResponse
import com.example.myapplication.Data.Response.HomeCharUpdateResponse
import com.example.myapplication.Data.Response.HomeSettingResponse
import com.example.myapplication.R
import com.example.myapplication.Retrofit.HomeIF
import com.example.myapplication.Retrofit.RetrofitService
import com.example.myapplication.databinding.FragmentChangeCharBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class ChangeCharFragment : Fragment() {

    lateinit var binding: FragmentChangeCharBinding
    private lateinit var changeCharAdapter: ChangeCharAdapter
    private var selectedCharacterId: Int = -1


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentChangeCharBinding.inflate(inflater, container, false)

        fetchCharacterData()

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // GridView 아이템 클릭 시 이벤트 처리
        binding.gvChange.onItemClickListener =
            AdapterView.OnItemClickListener { _, _, position, _ ->
                selectedCharacterId = (changeCharAdapter.getItem(position) as HomeChangeCharResponse.Character).characterId
                changeCharAdapter.setSelectedPosition(position)
            }

        // 완료 버튼 클릭 시 이벤트 처리
        binding.finishButton.setOnClickListener {
            if (selectedCharacterId != -1) {
                saveSelectedCharacter(selectedCharacterId)
                sendCharacterUpdateRequest(selectedCharacterId)
                Toast.makeText(requireContext(), "캐릭터가 선택되었습니다.", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(requireContext(), "캐릭터를 선택해주세요.", Toast.LENGTH_SHORT).show()
            }
        }
    }

    // HomeSettingFragment로 전환하는 메서드
    private fun navigateToHomeSettingFragment() {
        val fragmentTransaction = requireActivity().supportFragmentManager.beginTransaction()
        fragmentTransaction.replace(R.id.main_frm, HomeSettingFragment())
        fragmentTransaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
        fragmentTransaction.addToBackStack(null)
        fragmentTransaction.commit()
    }

    private fun fetchCharacterData() {
        val service = RetrofitService.createRetrofit(requireContext()).create(HomeIF::class.java)

        service.getCharacterData().enqueue(object : Callback<HomeChangeCharResponse> {
            override fun onResponse(
                call: Call<HomeChangeCharResponse>,
                response: Response<HomeChangeCharResponse>
            ) {
                if (response.isSuccessful) {
                    val apiResponse = response.body()

                    if (apiResponse != null && apiResponse.isSuccess) {
                        setupGridView(apiResponse.result)
                    } else {
                        Log.e("오류", "API 요청이 성공하지 못했습니다: ${apiResponse?.message}")
                    }
                } else {
                    Log.e("오류", "Response error: ${response.errorBody()?.string()}")
                }
            }

            override fun onFailure(call: Call<HomeChangeCharResponse>, t: Throwable) {
                Log.e("오류", "API 요청 실패: ${t.message}")
            }
        })
    }
    //완료 버튼 띄우는 메서드
    private fun showFinishButton() {
        binding.finishButton.visibility = View.VISIBLE
    }

    private fun setupGridView(characters: List<HomeChangeCharResponse.Character>) {
        changeCharAdapter = ChangeCharAdapter(requireContext(), characters) { selectedCharacter ->
            // 캐릭터가 선택된 아이템의 포지션을 찾아서 설정
            val selectedPosition = characters.indexOf(selectedCharacter)
            changeCharAdapter.setSelectedPosition(selectedPosition)

            // 캐릭터 선택 시 처리 로직
            selectedCharacterId = selectedCharacter.characterId
            saveSelectedCharacter(selectedCharacter.characterId)
            updateCharacterInView(selectedCharacter)

            showFinishButton()
        }

        binding.gvChange.adapter = changeCharAdapter
    }

    private fun saveSelectedCharacter(characterId: Int) {
        val sharedPreferences = requireContext().getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
        sharedPreferences.edit().putInt("selected_char", characterId).apply()
    }

    private fun updateCharacterInView(character: HomeChangeCharResponse.Character) {
        // 선택된 캐릭터 정보를 UI에 반영하는 로직
        // 현재는 단순히 선택된 캐릭터 알림만 구현해둠
        Toast.makeText(requireContext(), "선택된 캐릭터: ${character.name}", Toast.LENGTH_SHORT).show()
    }

    //수정 API 연동
    private fun sendCharacterUpdateRequest(characterId: Int) {
        val service = RetrofitService.createRetrofit(requireContext()).create(HomeIF::class.java)

        // PATCH 요청의 body 데이터
        val requestBody = CharacterUpdateRequest(characterId)
        service.updateCharacter(requestBody).enqueue(object : Callback<HomeCharUpdateResponse> {
            override fun onResponse(call: Call<HomeCharUpdateResponse>, response: Response<HomeCharUpdateResponse>) {
                if (response.isSuccessful) {
                    val apiResponse = response.body()

                    if (apiResponse != null && apiResponse.isSuccess) {
                        Toast.makeText(requireContext(), "캐릭터가 성공적으로 변경되었습니다.", Toast.LENGTH_SHORT).show()
                        navigateToHomeSettingFragment() // 성공 시 HomeSettingFragment로 전환
                    } else {
                        Toast.makeText(requireContext(), "요청에 실패했습니다: ${apiResponse?.message}", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    Toast.makeText(requireContext(), "응답 오류: ${response.errorBody()?.string()}", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<HomeCharUpdateResponse>, t: Throwable) {
                Toast.makeText(requireContext(), "API 요청 실패: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }


}





