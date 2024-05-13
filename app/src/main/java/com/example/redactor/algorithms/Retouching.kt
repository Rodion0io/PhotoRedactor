package com.example.redactor.algorithms

import android.graphics.Bitmap

class Retouching {

    public fun retuch(photo: Bitmap, radius: Double, centerX: Double, centerY: Double) : Bitmap{
        val firstPointX = centerX - radius;
        val firstPointY = centerY + radius;
        val secondPointX = centerX + radius;
        val secondPointY = centerY - radius;
        val sizeBlock = 7;

        for (i in firstPointX .. secondPointX step sizeBlock){
            for (j in firstPointY .. secondPointY step sizeBlock){

            }
        }
    }
}