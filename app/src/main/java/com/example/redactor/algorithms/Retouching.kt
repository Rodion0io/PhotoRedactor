package com.example.redactor.algorithms

import android.graphics.Bitmap
import android.graphics.Color
import kotlin.math.pow

class Retouching {

    private fun checkIncludeCircle(centerX: Double, centerY: Double, pointX: Int, pointY: Int, radius: Double): Boolean {
        var distanceX = (pointX - centerX).pow(2);
        var distanceY = (pointY - centerY).pow(2);
        if (distanceX - distanceY <= radius.pow(2)){
            return true;
        }
        else{
            return false;
        }
    }

    public fun retush(photo: Bitmap, centerX: Double, centerY: Double, radius: Double, ratioRetuch: Int) : Bitmap {
        var firstPointX: Int = (centerX - radius).toInt();
        var firstPointY: Int = (centerY + radius).toInt();
        var secondPointX: Int = (centerX + radius).toInt();
        var secondPointY: Int = (centerY - radius).toInt();

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
                if (checkIncludeCircle(centerX, centerY, i, j, radius)) {
                    val newColor = Color.argb(averageAlpha, averageRed, averageGreen, averageBlue);
                    photo.setPixel(i, j, newColor);
                }
            }
        }
        return photo;
    }
}