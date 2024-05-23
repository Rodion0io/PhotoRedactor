package com.example.redactor.algorithms

import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.drawable.Drawable
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.core.graphics.drawable.toBitmap

class UnsharpMasking {

    private fun Gaussian(inputBitmap: Bitmap, radius: Int) : Bitmap
    {
        val width = inputBitmap.width
        val height = inputBitmap.height
        val blurredBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)

        for (x in 0 until width) {
            for (y in 0 until height) {
                var rTotal = 0
                var gTotal = 0
                var bTotal = 0
                var count = 0

                for (i in -radius until radius + 1) {
                    for (j in -radius until radius + 1) {
                        val nx = x + i
                        val ny = y + j
                        if (nx >= 0 && nx < width && ny >= 0 && ny < height) {
                            val pixel = inputBitmap.getPixel(nx, ny)
                            rTotal += Color.red(pixel)
                            gTotal += Color.green(pixel)
                            bTotal += Color.blue(pixel)
                            count++
                        }
                    }
                }

                val r = rTotal / count
                val g = gTotal / count
                val b = bTotal / count
                blurredBitmap.setPixel(x, y, Color.rgb(r, g, b))
            }
        }

        return blurredBitmap;
    }

    @RequiresApi(Build.VERSION_CODES.O)
    public fun unsharpMasking(inputBitmap: Bitmap, radius: Int, amount: Float): Bitmap {

        val blurredBitmap = Gaussian(inputBitmap , radius)

        val width = inputBitmap.width
        val height = inputBitmap.height

        val outputBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)

        for (x in 0 until width)
        {
            for (y in 0 until height)
            {
                val originalPixel = inputBitmap.getPixel(x, y)
                val blurredPixel = blurredBitmap.getPixel(x, y)
                val red = Color.red(originalPixel) - Color.red(blurredPixel)
                val green = Color.green(originalPixel) - Color.green(blurredPixel)
                val blue = Color.blue(originalPixel) - Color.blue(blurredPixel)

                val Red = red * amount
                val Green = green * amount
                val Blue = blue * amount

                val enhancedMaskPixel = Color.rgb(Red, Green, Blue)

                val newR = (Color.red(originalPixel) + Color.red(enhancedMaskPixel)).coerceIn(0, 255)
                val newG = (Color.green(originalPixel) + Color.green(enhancedMaskPixel)).coerceIn(0, 255)
                val newB = (Color.blue(originalPixel) + Color.blue(enhancedMaskPixel)).coerceIn(0, 255)
                outputBitmap.setPixel(x, y, Color.rgb(newR, newG, newB))

            }
        }


        return outputBitmap
    }
}