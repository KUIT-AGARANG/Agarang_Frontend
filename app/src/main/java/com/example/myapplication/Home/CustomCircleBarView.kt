package com.example.myapplication.Home

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.RectF
import android.util.AttributeSet
import android.view.View
import java.lang.Math.min

class CustomCircleBarView : View {
    // 생성자
    constructor(context: Context?) : super(context)
    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs)

    // ARC(호)의 각도값을 관리할 변수
    var numProgress: Float = 0.0f

    // 뷰 그리기
    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        val paint = Paint()
        paint.isAntiAlias = true  // 경계선을 부드럽게

        val centerX = width / 2f
        val centerY = height / 2f
        val strokeWidth = min(width, height) * 0.03f  // 화면 크기의 3%로 설정
        paint.strokeWidth = strokeWidth

        val radius = (min(width, height) / 2f) - (strokeWidth / 2f) - 5f  // 경계에서 10f 만큼 여유를 둠

        // 배경 원
        paint.color = Color.parseColor("#80FEFFFE")
        paint.style = Paint.Style.STROKE
        canvas.drawCircle(centerX, centerY, radius, paint)

        // 프로그레스 원
        paint.color = Color.parseColor("#FFEB5F2A")
        val rect = RectF(centerX - radius, centerY - radius, centerX + radius, centerY + radius)
        canvas.drawArc(rect, -90f, numProgress, false, paint)
    }

    // 함수: 프로그레스바의 각도값을 변경하는 함수
    fun setProgress(num: Float) {
        // numProgress 값을 변경한다.
        numProgress = num

        // 뷰 갱신: 변경된 numProgress 값을 적용하여 뷰를 다시 그린다.
        invalidate()
    }
}

