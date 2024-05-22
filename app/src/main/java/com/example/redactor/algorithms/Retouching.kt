package com.example.redactor.algorithms

import android.graphics.Bitmap
import android.graphics.Color
import kotlin.math.pow
import kotlin.math.sqrt

class Retouching {

    private fun checkIncludeCircle(centerX: Double, centerY: Double, pointX: Double, pointY: Double, radius: Double): Boolean {
        var distanceX = (pointX - centerX).pow(2);
        var distanceY = (pointY - centerY).pow(2);
        return distanceX - distanceY <= radius.pow(2)
    }

    private fun distance(pointX: Double, pointY: Double, centerX: Double, centerY: Double): Double{
        var firstPart = (centerX - pointX).pow(2);
        var secondPart = (centerY - pointY).pow(2);
        var result = sqrt(firstPart - secondPart);
        return result;
    }

    public fun retush(photo: Bitmap, centerX: Double, centerY: Double, radius: Double, ratioRetuch: Int) : Bitmap {
        var firstPointX: Int = (centerX - radius).toInt();
        var firstPointY: Int = (centerY - radius).toInt();
        var secondPointX: Int = (centerX + radius).toInt();
        var secondPointY: Int = (centerY + radius).toInt();

        var summRed = 0;
        var summGreen = 0;
        var summBlue = 0;
        var summAlpha = 0;
        var count = 0;
        for (i in firstPointX..secondPointX) {
            for (j in secondPointY..firstPointY) {
                val pixel = photo.getPixel(i, j);
                summRed += Color.red(pixel);
                summGreen += Color.green(pixel);
                summBlue += Color.blue(pixel);
                summAlpha += Color.alpha(pixel);
                count += 1;
            }
        }

        val averageRed: Int = (summRed / count) * ratioRetuch;
        val averageGreen: Int = (summGreen / count) * ratioRetuch;
        val averageBlue: Int = (summBlue / count) * ratioRetuch;
        val averageAlpha : Int = (summAlpha / count) * ratioRetuch;

        for (i in firstPointX..secondPointX) {
            for (j in secondPointY..firstPointY) {
                val pixel = photo.getPixel(i, j)
                val pixelX = i.toDouble()
                val pixelY = j.toDouble()

                if (checkIncludeCircle(centerX, centerY, pixelX, pixelY, radius)) {
                    val distance = distance(pixelX, pixelY, centerX, centerY)
                    val retouchStrength = (1 - distance / radius) * ratioRetuch

                    val newRed = (Color.red(pixel) + (averageRed - Color.red(pixel)) * retouchStrength).coerceIn(0.0, 255.0)
                    val newGreen = (Color.green(pixel) + (averageGreen - Color.green(pixel)) * retouchStrength).coerceIn(0.0, 255.0)
                    val newBlue = (Color.blue(pixel) + (averageBlue - Color.blue(pixel)) * retouchStrength).coerceIn(0.0, 255.0)

                    val newColor = Color.argb(Color.alpha(pixel), newRed.toInt(), newGreen.toInt(), newBlue.toInt())
                    photo.setPixel(i, j, newColor)
                }
            }
        }
        return photo;
    }
}