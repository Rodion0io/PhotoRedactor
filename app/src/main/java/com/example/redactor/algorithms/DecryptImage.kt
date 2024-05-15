package com.example.redactor.algorithms

import android.graphics.Bitmap
import android.graphics.Color

class DecryptImage {
    public fun DeCryptImageInImg (pict: Bitmap): Bitmap
    {
        var newBitmap = Bitmap.createBitmap(pict.width, pict.height, pict.config)

        for(x in 0 until pict.width)
        {
            for (y in 0 until pict.height)
            {
                var pixel = pict.getPixel(x, y)
                var red = Color.red(pixel)
                var green = Color.green(pixel)
                var blue = Color.blue(pixel)
                var black = ((red.toString(2)).substring(5, 3) + green.toString(2).substring(6, 2) + blue.toString(2).substring(5, 3)).toInt(2)
                newBitmap.setPixel(x, y, Color.rgb(black, black, black))
            }
        }
        return newBitmap

    }
}
