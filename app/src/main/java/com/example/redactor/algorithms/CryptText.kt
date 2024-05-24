package com.example.redactor.algorithms
import android.graphics.Bitmap
import android.graphics.Color


class CryptText {
    public fun CryptTextInImg (pict: Bitmap, text: String): Bitmap
    {
        var x=0
        var y =0
        for(i in 0 until text.length)
        {
            var pixel = pict.getPixel(x, y)

            var red = Color.red(pixel)
            var green = Color.green(pixel)
            var blue = Color.blue(pixel)

            var newColor = replaceBits(red, green, blue, text[i].toInt())
            red = newColor[0]
            green = newColor[1]
            blue = newColor[2]
            pict.setPixel(x, y, Color.rgb( red, green, blue))


            if(x+1 == pict.width)
            {
                x=0;
                y++
            }
            else
            {
                x++
            }
        }
        var pixel = pict.getPixel(x, y)

        var red = Color.red(pixel)
        var green = Color.green(pixel)
        var blue = Color.blue(pixel)

        var newColor = replaceBits(red, green, blue, 255)
        red = newColor[0]
        green = newColor[1]
        blue = newColor[2]
        pict.setPixel(x, y, Color.rgb( red, green, blue))
        return (pict)
    }

    public  fun replaceBits(a: Int, b: Int, c: Int, d: Int): List<Int>
    {
        val binary = d.toString(2).padStart(8, '0')
        val A = a.toString(2).padStart(8, '0')
        val B= b.toString(2).padStart(8, '0')
        val C = c.toString(2).padStart(8, '0')
        val Apart = binary.substring(0, 3)
        val Bpart = binary.substring(3, 2)
        val Cpart = binary.substring(5, 3)
        val newA = A.substring(0,5) + Apart
        val newB = B.substring(0,6) + Bpart
        val newC = C.substring(0,5) + Cpart

        return listOf(newA.toInt(2), newB.toInt(2), newC.toInt(2))
    }

}