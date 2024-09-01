package com.example.myapplication.Memory

import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.myapplication.R
import com.example.myapplication.Data.SharedViewModel
import com.example.myapplication.databinding.FragmentSelectInstrumentBinding


class SelectInstrumentFragment : Fragment(), InstrumentAdapter.OnItemClickListener {

    private var _binding: FragmentSelectInstrumentBinding? = null
    private val binding get() = _binding!!
    private lateinit var rvInstruments: RecyclerView
    private val sharedViewModel : SharedViewModel by activityViewModels()
    private var questionId: String? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        _binding = FragmentSelectInstrumentBinding.inflate(inflater, container, false)
        rvInstruments=binding.rvInstruments

        val imageView =binding.ivBabyCharacter

        val sharedPreferences = requireContext().getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
        val selectedChar = sharedPreferences.getInt("selected_char", -1)

        if (selectedChar != -1) {
            // selectedChar 값을 사용하여 작업 수행
            imageView.setImageResource(selectedChar)
        }

        // 번들로 전달된 데이터 가져오기
        arguments?.let { bundle ->
            questionId = bundle.getString("id")
            Log.d("deepquestion-bundle",questionId.toString())
        }

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val instruments = listOf(
            InstrumentData(R.drawable.ic_piano, "피아노", false, instrumentCode = "PIANO"),
            InstrumentData(R.drawable.ic_flute, "플룻", instrumentCode = "FLUTE"),
            InstrumentData(R.drawable.ic_guitar, "기타", instrumentCode = "BASE_GUITAR"),
            InstrumentData(R.drawable.ic_violine, "바이올린", instrumentCode = "VIOLIN"),
            InstrumentData(R.drawable.ic_trumpet, "트럼펫", instrumentCode = "TRUMPET"),
            InstrumentData(R.drawable.ic_xylophone, "실로폰", false, instrumentCode = "XYLOPHONE"),
            InstrumentData(R.drawable.ic_harp, "하프", false, instrumentCode = "HARP"),
            InstrumentData(R.drawable.ic_cello, "첼로", instrumentCode = "CELLO"),
            InstrumentData(R.drawable.ic_saxophone, "색소폰", instrumentCode = "SAXOPHONE")
        )


//수정(클릭리스터 여기에)
        val adapter = InstrumentAdapter(instruments, this)
        binding.rvInstruments.layoutManager = GridLayoutManager(context, 3)
        binding.rvInstruments.adapter = adapter

    }
    override fun onItemClick(instrument: InstrumentData) {
        // 모든 악기의 선택 상태를 해제
        val instruments = (binding.rvInstruments.adapter as InstrumentAdapter).instruments
        instruments.forEach { it.isSelected = false }
        // 선택된 악기만 선택 상태로 변경
        instrument.isSelected = true
        binding.rvInstruments.adapter?.notifyDataSetChanged()

        //선택된 악기 저장
        sharedViewModel.setInstrument(instrument.instrumentCode)
        Log.e("SelectInstrumentFragment",instrument.instrumentCode)

        // 프래그먼트 전환
        val fragment = SelectGenreFragment()
        val bundle = Bundle()
        bundle.putString("id", questionId)
        fragment.arguments = bundle
        parentFragmentManager.beginTransaction()
            .replace(R.id.memory_frm, fragment)
            .addToBackStack(null)
            .commit()
    }
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}