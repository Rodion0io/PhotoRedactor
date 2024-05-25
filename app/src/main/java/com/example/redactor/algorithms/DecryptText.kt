package com.example.redactor.algorithms
import android.graphics.Bitmap
import android.graphics.Color

class DecryptText {
    public fun DeCryptTextInImg (pict: Bitmap): String
    {
        var text: String =""
        for(x in 0 until pict.width)
        {
            for(y in 0 until  pict.height)
            {
                var pixel = pict.getPixel(x, y)
                var red = Color.red(pixel)
                var green = Color.green(pixel)
                var blue = Color.blue(pixel)
                var symb =((red.toString(2)).padStart(8, '0').substring( 5,8) + green.toString(2).padStart(8, '0').substring( 6,8) + blue.toString(2).padStart(8, '0').substring( 5,8)).toInt(2)
                if(symb != 255)
                {
                    text += symb.toChar()
                }
                else
                {
                    return(text)
                }
            }
        }
        return("error")
    }
}