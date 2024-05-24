package com.example.redactor.algorithms

import android.graphics.Bitmap
import android.graphics.Color
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext


class Scale {
    private fun gaussian(inputBitmap: Bitmap, radius: Int): Bitmap {
        val width = inputBitmap.width
        val height = inputBitmap.height
        val blurredBitmap: Bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)

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


    public suspend fun imageScalling(bitmap: Bitmap, k: Double): Bitmap =
        withContext(Dispatchers.Default) {

        if (k < 1f) {
            return@withContext trillenarInterpolation(bitmap, k)
        }

        return@withContext BilinearInterpolation(bitmap, k)
    }

    private fun blend(firstSlice: Int, point2: Int, ds: Float): Int {
        val q = 1 - ds
        val qual = (Color.alpha(firstSlice) * q + Color.alpha(point2) * ds).toInt()
        val red = (Color.red(firstSlice) * q + Color.red(point2) * ds).toInt()
        val green = (Color.green(firstSlice) * q + Color.green(point2) * ds).toInt()
        val blue = (Color.blue(firstSlice) * q + Color.blue(point2) * ds).toInt()

        return Color.argb(qual, red, green, blue)
    }

    private fun Interpolation(
        point1: Int, point2: Int, point3: Int, point4: Int,
        dx: Float, dy: Float
    ): Int {
        val firstSlice = blend(point3, point4, dx)
        val secondSlice = blend(point1, point2, dx)

        return blend(firstSlice, secondSlice, dy)
    }


    private fun BilinearInterpolation(bitmap: Bitmap, k: Double): Bitmap {
        val NewWidth = (bitmap.width * k).toInt()
        val NewHeight = (bitmap.height * k).toInt()

        val ScaledBitmap = Bitmap.createBitmap(NewWidth, NewHeight, Bitmap.Config.ARGB_8888)

        val scaleX = bitmap.width / NewWidth.toFloat()
        val scaleY = bitmap.height / NewHeight.toFloat()

        for (x in 0 until NewWidth) {
            for (y in 0 until NewHeight) {

                val X = (x * scaleX)
                val Y = (y * scaleY)

                val x1 = X.toInt()
                val y1 = Y.toInt()

                val x2 = if (x1 + 1 < bitmap.width) x1 + 1 else x1
                val y2 = if (y1 + 1 < bitmap.height) y1 + 1 else y1

                val p1 = bitmap.getPixel(x1, y1)
                val p2 = bitmap.getPixel(x2, y1)
                val p3 = bitmap.getPixel(x1, y2)
                val p4 = bitmap.getPixel(x2, y2)

                val x3 = X - x1
                val y3 = Y - y1



                ScaledBitmap.setPixel(x, y, Interpolation(p1, p2, p3, p4, x3, y3))
            }
        }

        return ScaledBitmap
    }


    private fun trillenarInterpolation(bitmap: Bitmap, k: Double): Bitmap {
        val newWidth = (bitmap.width * k).toInt()
        val newHeight = (bitmap.height * k).toInt()

        val scaleX = bitmap.width / newWidth.toFloat()
        val scaleY = bitmap.height / newHeight.toFloat()

        val scaledBitmap = Bitmap.createBitmap(newWidth, newHeight, Bitmap.Config.ARGB_8888)

        for (x in 0 until newWidth) {
            for (y in 0 until newHeight) {
                val X = x * scaleX
                val Y = y * scaleY

                val x1 = X.toInt()
                val y1 = Y.toInt()

                val x2 = if (x1 + 1 < bitmap.width) x1 + 1 else x1
                val y2 = if (y1 + 1 < bitmap.height) y1 + 1 else y1

                val x3 = if (x1 > 0) x1 - 1 else x1
                val y3 = if (y1 > 0) y1 - 1 else y1

                val dx = X - x1;
                val dy = Y - y1;

                val p1 = bitmap.getPixel(x1, y1)
                val p2 = bitmap.getPixel(x2, y1)
                val p3 = bitmap.getPixel(x1, y2)
                val p4 = bitmap.getPixel(x2, y2)

                val p5 = bitmap.getPixel(x3, y2)
                val p6 = bitmap.getPixel(x3, y1)
                val p7 = bitmap.getPixel(x3, y3)
                val p8 = bitmap.getPixel(x1, y3)


                val slice1 = Interpolation(p1, p2, p3, p4, dx, dy)
                val slice2 = Interpolation(p5, p6, p7, p8, dx, dy)

                scaledBitmap.setPixel(x, y, blend(slice1, slice2, 1f))
            }
        }

        return gaussian(scaledBitmap, 2)
    }


}


