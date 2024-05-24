package com.example.redactor.algorithms

import android.graphics.Bitmap
import android.graphics.Color
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class Scale {

    private fun gaussian(inputBitmap: Bitmap, radius: Int) : Bitmap
    {
        val width = inputBitmap.width
        val height = inputBitmap.height
        val blurredBitmap : Bitmap = Bitmap.createBitmap(width, height,
            Bitmap.Config.ARGB_8888)

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

        return blurredBitmap
    }


    public suspend fun imageScalling( bitmap: Bitmap,  k : Double ) : Bitmap =
        withContext(Dispatchers.Default)
    {

        if ( k  < 1.0)
        {
            return@withContext trillenarInterpolation(bitmap, k)
        }

        return@withContext bilinearInterpolation(bitmap, k)
    }

    private fun bilinearInterpolation(bitmap: Bitmap, k : Double) : Bitmap
    {
        val NewWidth  = (bitmap.width * k).toInt()
        val NewHeight = (bitmap.height * k).toInt()
        val ScaledBitmap = Bitmap.createBitmap(NewWidth, NewHeight,
            Bitmap.Config.ARGB_8888)

        for (x in 0 until NewWidth)
        {
            for (y in 0 until NewHeight)
            {

                val X = (x / k)
                val Y = (y / k)

                val x1 = X.toInt()
                val y1 = Y.toInt()

                val x2 = if (x1 + 1 < bitmap.width ) x1 + 1 else x1
                val y2 = if (y1 + 1 < bitmap.height ) y1 + 1 else y1

                val p1 = bitmap.getPixel(x1, y1)
                val p2 = bitmap.getPixel(x2, y1)
                val p3 = bitmap.getPixel(x1, y2)
                val p4 = bitmap.getPixel(x2, y2)

                val x3 = X - x1
                val y3 = Y - y1

                val red1 = (1 - x3) * (1 - y3) * Color.red(p1)
                val red2 = x3 * (1 - y3) * Color.red(p2)
                val red3 = (1 - x3) * y3 * Color.red(p3)
                val red4 = x3 * y3 * Color.red(p4)

                val red = (red1 + red2 + red3 + red4)

                val green1 = (1 - x3) * (1 - y3) * Color.green(p1)
                val green2 = x3 * (1 - y3) * Color.green(p2)
                val green3 =  (1 - x3) * y3 * Color.green(p3)
                val green4 = x3 * y3 * Color.green(p4)

                val green = (green1 + green2 + green3 + green4)

                val blue1 = (1 - x3) * (1 - y3) * Color.blue(p1)
                val blue2 = x3 * (1 - y3) * Color.blue(p2)
                val blue3 =  (1 - x3) * y3 * Color.blue(p3)
                val blue4 = x3 * y3 * Color.blue(p4)

                val blue = (blue1 + blue2 + blue3 + blue4)

                ScaledBitmap.setPixel(x, y, Color.rgb(red.toInt(), green.toInt(), blue.toInt())
                )
            }
        }

        return ScaledBitmap
    }


    private fun trillenarInterpolation(bitmap: Bitmap, k: Double): Bitmap
    {
        val newWidth = (bitmap.width * k).toInt()
        val newHeight = (bitmap.height * k).toInt()
        val scaledBitmap = Bitmap.createBitmap(newWidth, newHeight,
            Bitmap.Config.ARGB_8888)

        for (x in 0 until newWidth)
        {
            for (y in 0 until newHeight)
            {
                val X = x / k
                val Y = y / k

                val x1 = X.toInt()
                val y1 = Y.toInt()

                val x2 = if (x1 + 1 < bitmap.width) x1 + 1 else x1
                val y2 = if (y1 + 1 < bitmap.height) y1 + 1 else y1

                val p1 = bitmap.getPixel(x1, y1)
                val p2 = bitmap.getPixel(x2, y1)
                val p3 = bitmap.getPixel(x1, y2)
                val p4 = bitmap.getPixel(x2, y2)

                val fx = X - x1
                val fy = Y - y1


                val red1 = (1 - fx) * Color.red(p1) + fx * Color.red(p2)
                val red2 = (1 - fx) * Color.red(p3) + fx * Color.red(p4)

                val green1 = (1 - fx) * Color.green(p1) + fx * Color.green(p2)
                val green2 = (1 - fx) * Color.green(p3) + fx * Color.green(p4)

                val blue1 = (1 - fx) * Color.blue(p1) + fx * Color.blue(p2)
                val blue2 = (1 - fx) * Color.blue(p3) + fx * Color.blue(p4)

                val blue = (1 - fy) * blue1 + fy * blue2
                val green = (1 - fy) * green1 + fy * green2
                val red = (1 - fy) * red1 + fy * red2

                scaledBitmap.setPixel(x, y, Color.rgb(red.toInt(), green.toInt(), blue.toInt())
                )
            }
        }

        gaussian(scaledBitmap, 100)
        return scaledBitmap
    }

}
