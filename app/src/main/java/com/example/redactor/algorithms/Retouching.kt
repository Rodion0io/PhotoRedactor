package com.example.redactor.algorithms

import android.graphics.Bitmap
import android.graphics.Color
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlin.math.pow
import kotlin.math.sqrt


class Retouching {

    private fun checkIncludeCircle(centerX: Double, centerY: Double, pointX: Double, pointY: Double, radius: Double): Boolean {
        val distanceX = (pointX - centerX).pow(2)
        val distanceY = (pointY - centerY).pow(2)
        return distanceX + distanceY <= radius.pow(2)
    }

    private fun distance(pointX: Double, pointY: Double, centerX: Double, centerY: Double): Double {
        val firstPart = (centerX - pointX).pow(2)
        val secondPart = (centerY - pointY).pow(2)
        return sqrt(firstPart + secondPart)
    }

    suspend fun retush(photo: Bitmap, centerX: Double, centerY: Double, radius: Double, ratioRetuch: Int) : Bitmap =
        withContext(Dispatchers.Default) {
            val centerXInt = centerX.toInt()
            val centerYInt = centerY.toInt()
            val radiusInt = radius.toInt()

            val firstPointX = maxOf(centerXInt - radiusInt, 0)
            val firstPointY = maxOf(centerYInt - radiusInt, 0)
            val secondPointX = minOf(centerXInt + radiusInt, photo.width - 1)
            val secondPointY = minOf(centerYInt + radiusInt, photo.height - 1)

            var summRed = 0
            var summGreen = 0
            var summBlue = 0
            var summAlpha = 0
            var count = 0

            // Calculate average color values within the retouching area
            for (i in firstPointX..secondPointX) {
                for (j in firstPointY..secondPointY) {
                    if (checkIncludeCircle(centerX, centerY, i.toDouble(), j.toDouble(), radius)) {
                        val pixel = photo.getPixel(i, j)
                        summRed += Color.red(pixel)
                        summGreen += Color.green(pixel)
                        summBlue += Color.blue(pixel)
                        summAlpha += Color.alpha(pixel)
                        count++
                    }
                }
            }

            val averageRed: Int = (summRed / count)
            val averageGreen: Int = (summGreen / count)
            val averageBlue: Int = (summBlue / count)
            val averageAlpha : Int = (summAlpha / count)

            // Apply retouching to each pixel within the retouching area while preserving the color distribution
            for (i in firstPointX..secondPointX) {
                for (j in firstPointY..secondPointY) {
                    if (checkIncludeCircle(centerX, centerY, i.toDouble(), j.toDouble(), radius)) {
                        val pixel = photo.getPixel(i, j)
                        val distance = distance(i.toDouble(), j.toDouble(), centerX, centerY)
                        val retouchStrength = (1 - distance / radius) * ratioRetuch

                        // Coefficient for preserving color distribution
                        val preserveCoefficient = 1.0 - (1.0 - retouchStrength / ratioRetuch)

                        val newRed = (Color.red(pixel) + (averageRed - Color.red(pixel)) * preserveCoefficient).coerceIn(0.0,255.0)
                        val newGreen = (Color.green(pixel) + (averageGreen - Color.green(pixel)) * preserveCoefficient).coerceIn(0.0,255.0)
                        val newBlue = (Color.blue(pixel) + (averageBlue - Color.blue(pixel)) * preserveCoefficient).coerceIn(0.0,255.0)
                        val newAlpha = (Color.alpha(pixel) + (averageAlpha - Color.alpha(pixel)) * preserveCoefficient).coerceIn(0.0,255.0)

                        val newColor = Color.argb(newAlpha.toInt(), newRed.toInt(), newGreen.toInt(), newBlue.toInt())
                        photo.setPixel(i, j, newColor)
                    }
                }
            }
            return@withContext photo
        }
}

