package com.example.myapplication

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.GridView


class ChangeChar2Fragment : Fragment() {

    private val imageResources = intArrayOf(
        R.drawable.mouse_2,R.drawable.cow_2,R.drawable.tiger_2,R.drawable.rabbit_2,R.drawable.dragon_2, R.drawable.snake_2,
        R.drawable.horse_2,R.drawable.sheep_2,R.drawable.monkey_2,R.drawable.chick_2,R.drawable.dog_2,R.drawable.pig_2
    )
    private val names = arrayOf(
        "쥐", "소", "호랑이", "토끼", "용", "뱀", "말", "양", "원숭이", "닭", "개", "돼지"
    )
    private val descriptions = arrayOf(
        "똑똑하고 재치 있는 아기가 될 거예요. 작은 손발로도 무엇이든 잘 할 수 있는 만능 재주꾼!",
        "성실하고 꾸준히 노력하는 아기가 될 거예요. 느긋하면서도 듬직한 작은 힘꾼!",
        "씩씩하고 용맹한 아기가 될 거예요. 어디서든 당당하게 자신을 표현할 수 있는 강한 리더!",
        "상냥하고 부드러운 아기가 될 거예요. 조용하고 차분하게 주위를 따뜻하게 만드는 매력쟁이!",
        "지혜롭고 카리스마 있는 아기가 될 거예요. 언제나 빛나는 존재로 성장할 작은 리더!",
        "깊이 있는 지혜와 통찰력을 가진 아기가 될 거예요. 차분하고 세심하게 모든 것을 알아가는 꼬마 탐정!",
        "활발하고 자유로운 아기가 될 거예요. 어디든 자유롭게 달려가는 작은 모험가!",
        "온화하고 따뜻한 마음을 가진 아기가 될 거예요. 모두에게 사랑받는 부드러운 작은 천사!",
        "재치 있고 창의적인 아기가 될 거예요. 유쾌하게 모두를 웃게 만드는 장난꾸러기!",
        "성실하고 부지런한 아기가 될 거예요. 아침을 깨우는 작은 부지런쟁이!",
        "진실하고 충성스러운 아기가 될 거예요. 언제나 친구들과 함께하는 든든한 작은 수호자!",
        "풍요롭고 복이 많은 아기가 될 거예요. 웃음이 넘치고 모두에게 사랑받는 작은 행복이!"
    )
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_change_char2, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val gridView: GridView = view.findViewById(R.id.gv_change)
        val adapter = ChangeChar2Adapter(requireContext(), imageResources, names, descriptions)
        gridView.adapter = adapter
    }


}