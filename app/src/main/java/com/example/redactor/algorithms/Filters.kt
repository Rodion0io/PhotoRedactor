package com.example.redactor.algorithms

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
import android.graphics.drawable.BitmapDrawable
class Filters {

    public fun mirrorImage(inputImage: Bitmap): Bitmap
    {
        val newBitmap = Bitmap.createBitmap(inputImage.width, inputImage.height, inputImage.config)
        for (x in 0 until inputImage.width)
        {
            for (y in 0 until inputImage.height)
            {
                val pixelLeft = inputImage.getPixel(x, y)
                newBitmap.setPixel(inputImage.width - x -1, y, pixelLeft)
            }
        }

        return newBitmap
    }

    public fun negativeImage(inputImage: Bitmap): Bitmap
    {
        val newBitmap = Bitmap.createBitmap(inputImage.width, inputImage.height, inputImage.config)
        for (x in 0 until inputImage.width)
        {
            for (y in 0 until inputImage.height)
            {
                val pixel = inputImage.getPixel(x, y)

                val red = 255 - Color.red(pixel)
                val green = 255 - Color.green(pixel)
                val blue = 255 - Color.blue(pixel)


                newBitmap.setPixel(x, y, Color.rgb( red, green, blue))
            }
        }

        return newBitmap
    }
    public fun mosaicImage(inputImage: Bitmap): Bitmap
    {
        val newBitmap = Bitmap.createBitmap(inputImage.width, inputImage.height, inputImage.config)
        val chunkSize = 100
        for (x in 0 until inputImage.width step chunkSize)
        {
            for (y in 0 until inputImage.height step chunkSize)
            {
                var midRed = 0
                var midGreen = 0
                var midBlue = 0
                var count = 0

                for (i in x until minOf(x + chunkSize, inputImage.width))
                {
                    for (j in y until minOf(y + chunkSize, inputImage.height))
                    {
                        val pixel = inputImage.getPixel(i, j)
                        midRed += Color.red(pixel)
                        midGreen += Color.green(pixel)
                        midBlue += Color.blue(pixel)
                        count++
                    }
                }

                midRed = midRed / count
                midGreen = midGreen / count
                midBlue = midBlue / count

                for (i in x until minOf(x + chunkSize, inputImage.width))
                {
                    for (j in y until minOf(y + chunkSize, inputImage.height))
                    {
                        newBitmap.setPixel(i, j, Color.rgb(midRed, midGreen, midBlue))
                    }
                }
            }
        }

        return newBitmap
    }

    public fun redImage(inputImage: Bitmap): Bitmap
        {
        val newBitmap = Bitmap.createBitmap(inputImage.width, inputImage.height, inputImage.config)
        for (x in 0 until inputImage.width)
        {
            for (y in 0 until inputImage.height)
            {
                val pixel = inputImage.getPixel(x, y)
                newBitmap.setPixel(x, y, Color.rgb(Color.red(pixel), 0, 0))
            }
        }

        return newBitmap
        }

    public fun greenImage(inputImage: Bitmap): Bitmap
    {
        val newBitmap = Bitmap.createBitmap(inputImage.width, inputImage.height, inputImage.config)
        for (x in 0 until inputImage.width)
        {
            for (y in 0 until inputImage.height)
            {
                val pixel = inputImage.getPixel(x, y)
                newBitmap.setPixel(x, y, Color.rgb(0, Color.green(pixel), 0))
            }
        }

        return newBitmap
    }

    public fun blueImage(inputImage: Bitmap): Bitmap
    {
        val newBitmap = Bitmap.createBitmap(inputImage.width, inputImage.height, inputImage.config)
        for (x in 0 until inputImage.width)
        {
            for (y in 0 until inputImage.height)
            {
                val pixel = inputImage.getPixel(x, y)
                newBitmap.setPixel(x, y, Color.rgb(0, 0, Color.blue(pixel)))
            }
        }

        return newBitmap
    }

}