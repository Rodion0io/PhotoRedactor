package com.example.redactor.algorithms

import android.graphics.Bitmap
import android.graphics.Color
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlin.math.*

class Rotate {
    private fun makeRadian(angle: Double): Double {
        return angle * (PI / 180.0)
    }

    suspend fun rotatePicture(pict: Bitmap, angle: Double): Bitmap =
        withContext(Dispatchers.Default) {
            val radianAngle = makeRadian(angle)
            val width = pict.width
            val height = pict.height
            val centerX = width / 2.0
            val centerY = height / 2.0

            val corners = arrayOf(
                doubleArrayOf(-centerX, -centerY),
                doubleArrayOf(centerX, -centerY),
                doubleArrayOf(-centerX, centerY),
                doubleArrayOf(centerX, centerY)
            )

            val newCorners = corners.map { (x, y) ->
                val newX = x * cos(radianAngle) - y * sin(radianAngle)
                val newY = x * sin(radianAngle) + y * cos(radianAngle)
                doubleArrayOf(newX, newY)
            }

            val x = newCorners.map { it[0] }
            val y = newCorners.map { it[1] }

            val newWidth = ceil(x.maxOrNull()!! - x.minOrNull()!!).toInt()
            val newHeight = ceil(y.maxOrNull()!! - y.minOrNull()!!).toInt()

            val outputBitmap = Bitmap.createBitmap(newWidth, newHeight, Bitmap.Config.ARGB_8888)
            val newCenterX = newWidth / 2.0
            val newCenterY = newHeight / 2.0


            for (newX in 0 until newWidth) {
                for (newY in 0 until newHeight) {
                    val originalX = ((newX - newCenterX) * cos(-radianAngle) - (newY - newCenterY) * sin(-radianAngle) + centerX).toInt()
                    val originalY   = ((newX - newCenterX) * sin(-radianAngle) + (newY - newCenterY) * cos(-radianAngle) + centerY).toInt()

                    if (originalX in 0 until width && originalY in 0 until height) {
                        outputBitmap.setPixel(newX, newY, pict.getPixel(originalX, originalY))
                    } else {
                        outputBitmap.setPixel(newX, newY, Color.TRANSPARENT)
                    }
                }
            }

            return@withContext outputBitmap
        }
}
