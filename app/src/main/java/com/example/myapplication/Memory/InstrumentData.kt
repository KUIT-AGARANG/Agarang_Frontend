package com.example.myapplication.Memory

data class InstrumentData(
    val imageResId: Int,
    val instrumentName: String,
    val shouldCrop: Boolean = true,
    var isSelected: Boolean = false,
    val instrumentCode: String
)