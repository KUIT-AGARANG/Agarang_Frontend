package com.example.myapplication.Memory

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.myapplication.R

class MemoryMainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_memory_main)


        val fragment=PictureFragment()
        supportFragmentManager.beginTransaction()
            .replace(R.id.memory_frm,fragment)
            .commit()
    }
}