package com.example.redactor

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.PointF
import android.os.Bundle
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import androidx.appcompat.app.AppCompatActivity


class SplineActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(DrawingView(this))
    }

    inner class DrawingView(context: Context) : View(context) {

        private val points = mutableListOf<PointF>()
        private val paint = Paint()

        init {
            // Устанавливаем цвет и стиль кисти для рисования точек
            paint.color = Color.RED
            paint.style = Paint.Style.FILL_AND_STROKE
        }

        override fun onTouchEvent(event: MotionEvent): Boolean {
            // Обработка события касания экрана
            when (event.action) {
                MotionEvent.ACTION_DOWN -> {
                    // При нажатии добавляем новую точку в массив и перерисовываем вид
                    val point = PointF(event.x, event.y)
                    points.add(point)
                    invalidate()
                }
            }
            return true
        }

        override fun onDraw(canvas: Canvas) {
            // Рисуем все точки из массива на канвас
            for (point in points) {
                canvas.drawCircle(point.x, point.y, 20f, paint)
            }
            paint.strokeWidth = 5f
            for(i in 0 until points.count() -1)
            {
                canvas.drawLine(points[i].x, points[i].y, points[i+1].x, points[i+1].y, paint)
            }

        }
    }
}