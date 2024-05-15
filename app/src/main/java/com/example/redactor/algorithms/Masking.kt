package com.example.redactor.algorithms

import android.graphics.Bitmap
import android.graphics.Rect

class Masking {

    public fun Dr (bitmap: Bitmap, radius: Int): Bitmap
    {

        val output = Bitmap.createBitmap(bitmap.width, bitmap.height, bitmap.config)

        val rect = Rect(0, 0, bitmap.width, bitmap.height)

        val blurred = Bitmap.createBitmap(bitmap.width, bitmap.height, bitmap.config)

        for (x in 0 until bitmap.width) {
            for (y in 0 until bitmap.height) {
                var r = 0
                var g = 0
                var b = 0
                var a = 0
                var count = 0

                for (dx in -radius..radius) {
                    for (dy in -radius..radius) {
                        val nx = x + dx
                        val ny = y + dy

                        if (nx in 0 until bitmap.width && ny in 0 until bitmap.height) {
                            val pixel = bitmap.getPixel(nx, ny)
                            r += pixel shr 16 and 0xFF
                            g += pixel shr 8 and 0xFF
                            b += pixel and 0xFF
                            a += pixel shr 24 and 0xFF
                            count++
                        }
                    }
                }

                r /= count
                g /= count
                b /= count
                a /= count

                val newPixel = (a shl 24) or (r shl 16) or (g shl 8) or b
                blurred.setPixel(x, y, newPixel)
            }
        }

        return blurred
    }
}