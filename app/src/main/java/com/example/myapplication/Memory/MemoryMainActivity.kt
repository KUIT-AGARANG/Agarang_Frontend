package com.example.myapplication.Memory

import android.os.Bundle
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import com.example.myapplication.R
import com.example.myapplication.databinding.ActivityMemoryMainBinding

class MemoryMainActivity : AppCompatActivity() {
    lateinit var binding: ActivityMemoryMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_memory_main)

        val window=window
        window.setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS, WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS)

        val fragment=SelectInstrumentFragment()
        supportFragmentManager.beginTransaction()
            .replace(R.id.memory_frm,fragment)
            .commit()
    }
}