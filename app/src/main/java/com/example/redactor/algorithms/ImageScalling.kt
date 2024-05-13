package com.example.redactor.algorithms

import android.graphics.Bitmap
import android.graphics.Color

fun ImageScalling(bitmap: Bitmap,  k : Double) : Bitmap
{
    val ScaledBitmap : Bitmap

    if ( k % 2 < 1)
    {
        ScaledBitmap = TrillenarInterpolation(bitmap, k)
    }
    else
    {
         ScaledBitmap =  BilinearInterpolation(bitmap, k)
    }
    return ScaledBitmap
}

fun BilinearInterpolation(bitmap: Bitmap, k : Double) : Bitmap
{
    val NewWidth  = (bitmap.width * k).toInt()
    val NewHeight = (bitmap.height * k).toInt()
    val ScaledBitmap = Bitmap.createBitmap(NewWidth, NewHeight, Bitmap.Config.ARGB_8888)

    for (x in 0 until NewWidth)
    {
        for (y in 0 until NewHeight)
        {

            val X = (x / k).toInt()
            val Y = (y / k).toInt()

            val X1 = if (X + 1 < bitmap.width) X + 1 else X
            val Y1 = if (Y + 1 < bitmap.height) Y + 1 else Y

            val p1 = bitmap.getPixel(X, Y)
            val p2 = bitmap.getPixel(X1, Y)
            val p3 = bitmap.getPixel(X, Y1)
            val p4 = bitmap.getPixel(X1, Y1)

            val fx = x - X
            val fy = y - Y

            val red1 = (1 - fx) * (1 - fy) * Color.red(p1)
            val red2 = fx * (1 - fy) * Color.red(p2)
            val red3 = (1 - fx) * fy * Color.red(p3)
            val red4 = fx * fy * Color.red(p4)

            val red = (red1 + red2 + red3 + red4).toInt()

            val green1 = (1 - fx) * (1 - fy) * Color.green(p1)
            val green2 = fx * (1 - fy) * Color.green(p2)
            val green3 =  (1 - fx) * fy * Color.green(p3)
            val green4 = fx * fy * Color.green(p4)

            val green = (green1 + green2 + green3 + green4).toInt()

            val blue1 = (1 - fx) * (1 - fy) * Color.blue(p1)
            val blue2 = fx * (1 - fy) * Color.blue(p2)
            val blue3 =  (1 - fx) * fy * Color.blue(p3)
            val blue4 = fx * fy * Color.blue(p4)

            val blue = (blue1 + blue2 + blue3 + blue4).toInt()

            ScaledBitmap.setPixel(x, y, Color.rgb(red, green, blue) )
        }
    }

    return ScaledBitmap
}


fun TrillenarInterpolation(bitmap: Bitmap, k: Double): Bitmap
{
    val newWidth = (bitmap.width * k).toInt()
    val newHeight = (bitmap.height * k).toInt()
    val scaledBitmap = Bitmap.createBitmap(newWidth, newHeight, Bitmap.Config.ARGB_8888)

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

            scaledBitmap.setPixel(x, y, Color.rgb(red.toInt(), green.toInt(), blue.toInt()) )
        }
    }
    return scaledBitmap
}


