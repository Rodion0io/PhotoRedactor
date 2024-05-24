package com.example.redactor

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.PointF
import android.os.Bundle
import android.view.MotionEvent
import android.view.View
import android.widget.Button
import android.widget.RelativeLayout
import androidx.appcompat.app.AppCompatActivity
import android.graphics.Path

class SplineActivity : AppCompatActivity() {

    private lateinit var drawingView: DrawingView
    public var switch = false;
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        drawingView = DrawingView(this)
        setContentView(drawingView)

        val splineButton = Button(this)
        splineButton.text = "Сплайны"
        splineButton.setOnClickListener {
            drawingView.makeSplines()
        }

        val layout = RelativeLayout(this)
        layout.addView(splineButton)

        val layoutParams = RelativeLayout.LayoutParams(
            RelativeLayout.LayoutParams.WRAP_CONTENT,
            RelativeLayout.LayoutParams.WRAP_CONTENT
        )
        layoutParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM)
        splineButton.layoutParams = layoutParams

        addContentView(layout, layoutParams)
    }

    inner class DrawingView(context: Context) : View(context) {
        private var points = mutableListOf<PointF>()
        private val paint = Paint()
        private val path = Path()

        init {
            paint.color = Color.RED
            paint.style = Paint.Style.FILL_AND_STROKE
        }

        override fun onTouchEvent(event: MotionEvent): Boolean {
            when (event.action) {
                MotionEvent.ACTION_DOWN -> {
                    val point = PointF(event.x, event.y)
                    points.add(point)

                    invalidate()
                }
            }
            return true
        }

        override fun onDraw(canvas: Canvas) {
            if(switch == false) {
                path.reset()
                paint.style = Paint.Style.FILL_AND_STROKE
                for (point in points) {
                    canvas.drawCircle(point.x, point.y, 20f, paint)
                }
                paint.strokeWidth = 5f
                for (i in 0 until points.count() - 1) {
                    canvas.drawLine(
                        points[i].x, points[i].y, points[i + 1].x, points[i + 1].y, paint
                    )
                }
            }
            else
            {
                path.reset()
                paint.style = Paint.Style.FILL_AND_STROKE
                paint.color = Color.GREEN
                for (point in points) {
                    canvas.drawCircle(point.x, point.y, 20f, paint)
                }
                path.moveTo(points[0].x, points[0].y)
                paint.color = Color.RED
                paint.style = Paint.Style.STROKE
                for (i in 0 until points.size - 1) {
                    val startX = points[i].x
                    val startY = points[i].y
                    val endX = points[i + 1].x
                    val endY = points[i + 1].y

                    val midX = (startX + endX) / 2
                    val midY = (startY + endY) / 2

                    path.quadTo( startX, startY, midX, midY)

                }
                val lastPoint = points.last()
                path.lineTo(lastPoint.x, lastPoint.y)
                canvas.drawPath(path, paint)
            }
        }

        fun makeSplines() {
            switch = !switch;
            invalidate()
        }


    }
}


