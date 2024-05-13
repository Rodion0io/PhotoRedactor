package com.example.redactor.algorithms

import android.graphics.Bitmap
import android.graphics.Color
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.coroutineScope
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking


class Rotate {


    companion object{
        private fun makeRadian(angle: Double): Double {
            return angle * (PI / 180.0)
        }
        suspend fun rotatePicture(pict: Bitmap, angle: Double): Bitmap = coroutineScope {
            val radianAngle = makeRadian(angle)
            val width = pict.width
            val height = pict.height
            val centerX = width / 2.0
            val centerY = height / 2.0

            var maxiX = Double.NEGATIVE_INFINITY
            var miniX = Double.POSITIVE_INFINITY
            var maxiY = Double.NEGATIVE_INFINITY
            var miniY = Double.POSITIVE_INFINITY


            launch(Dispatchers.Default) {
                for (i in 0 until height) {
                    for (j in 0 until width) {
                        val newX = (i - centerY) * cos(radianAngle) - (j - centerX) * sin(radianAngle) + centerX
                        val newY = (j - centerX) * cos(radianAngle) + (i - centerY) * sin(radianAngle) + centerY
                        maxiX = maxOf(newX, maxiX)
                        maxiY = maxOf(newY, maxiY)
                        miniX = minOf(newX, miniX)
                        miniY = minOf(newY, miniY)
                    }
                }
            }


            val newWidth = (maxiX - miniX).toInt()
            val newHeight = (maxiY - miniY).toInt()

            val outputBitmap = Bitmap.createBitmap(newWidth, newHeight, Bitmap.Config.ARGB_8888)

            for (newX in 0 until newWidth) {
                for (newY in 0 until newHeight) {
                    val originalX = ((newX + miniX - centerX) * cos(radianAngle) + (newY + miniY - centerY) * sin(radianAngle) + centerX).toInt()
                    val originalY = ((newY + miniY - centerY) * cos(radianAngle) - (newX + miniX - centerX) * sin(radianAngle) + centerY).toInt()

                    if (originalX in 0 until width && originalY in 0 until height) {
                        outputBitmap.setPixel(newX, newY, pict.getPixel(originalX, originalY))
                    }
                    else{
                        outputBitmap.eraseColor(Color.BLACK)
                    }
                }
            }

            outputBitmap
        }
    }


}