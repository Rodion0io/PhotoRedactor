package com.example.redactor.algorithms

import android.graphics.Bitmap
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin


class Rotate {
    private fun makeRadian(angle:Double):Double{
        return angle * (180.0 / PI)
    }

    public fun rotatePicture(pict: Bitmap, angle: Double):Bitmap{
        var radianAngle: Double = makeRadian(angle);
        val width = pict.width;
        val height = pict.height;
        var maxiX = Double.NEGATIVE_INFINITY;
        var miniX = Double.POSITIVE_INFINITY;
        var maxiY = Double.NEGATIVE_INFINITY;
        var miniY = Double.POSITIVE_INFINITY;

            for (i in 0 until height){
                for (j in 0 until width){
                    val newX = i * cos(radianAngle) + j * sin(radianAngle)
                    val newY = j * cos(radianAngle) - i * sin(radianAngle)
                    maxiX = maxOf(newX, maxiX);
                    maxiY = maxOf(newY, maxiY);
                    miniX = minOf(newX, miniX);
                    miniY = minOf(newY, miniY)
            }
        }

        var newWidth = maxiX - miniX;
        var newHeight = maxiY - miniY;


        val outputBitmap = Bitmap.createBitmap(newWidth.toInt(), newHeight.toInt(), pict.config)

        for (newX in 0 until newWidth.toInt()) {
            for (newY in 0 until newHeight.toInt()) {
                val originalX = ((newX * cos(radianAngle) - newY * sin(radianAngle)) + miniX).toInt()
                val originalY = ((newY * cos(radianAngle) + newX * sin(radianAngle)) + miniY).toInt()

                if (originalX in 0 until width && originalY in 0 until height) {
                    outputBitmap.setPixel(newX, newY, pict.getPixel(originalX, originalY))
                }
            }
        }

        return outputBitmap
    }
}