package com.example.redactor.algorithms
import android.graphics.Bitmap
import android.graphics.Color


class CryptText {
    public fun CryptTextInImg (pict: Bitmap, text: String): Bitmap
    {

        var letter =0;
        var newBitmap = Bitmap.createBitmap(pict.width, pict.height, pict.config)
        for (x in 0 until pict.width)
        {
            for (y in 0 until pict.height)
            {
                if(letter  < text.length  -1)
                {
                    var pixel = pict.getPixel(x, y)

                    var red = Color.red(pixel)
                    var green = Color.green(pixel)
                    var blue = Color.blue(pixel)
                    var newColor = replaceBits(red, green, blue, text[letter].code)
                    red = newColor[0]
                    green = newColor[1]
                    blue = newColor[2]
                    newBitmap.setPixel(x, y, Color.rgb( red, green, blue))
                    letter++
                }
                else if (letter == text.length  -1)
                {
                    var pixel = pict.getPixel(x, y)

                    var red = Color.red(pixel)
                    var green = Color.green(pixel)
                    var blue = Color.blue(pixel)
                    var newColor = replaceBits(red, green, blue, 255)
                    red = newColor[0]
                    green = newColor[1]
                    blue = newColor[2]
                    newBitmap.setPixel(x, y, Color.rgb( red, green, blue))
                    letter++
                }
                else
                {
                    val pixel = pict.getPixel(x, y)
                    newBitmap.setPixel(x, y, pixel)
                }

            }
        }

        return (newBitmap)
    }

    public  fun replaceBits(a: Int, b: Int, c: Int, d: Int): List<Int>
    {
        val binary = d.toString(2).padStart(8, '0')
        val A = a.toString(2).padStart(8, '0')
        val B= b.toString(2).padStart(8, '0')
        val C = c.toString(2).padStart(8, '0')
        val Apart = binary.substring(0, 3)
        val Bpart = binary.substring(3, 5)
        val Cpart = binary.substring(5, 8)
        val newA = A.substring(0,5) + Apart
        val newB = B.substring(0,6) + Bpart
        val newC = C.substring(0,5) + Cpart

        return listOf(newA.toInt(2), newB.toInt(2), newC.toInt(2))
    }


}