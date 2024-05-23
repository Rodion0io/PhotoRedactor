package com.example.redactor.actions

import android.content.Context
import android.graphics.Bitmap
import android.os.Environment
import android.provider.MediaStore
import android.widget.Toast
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*

class Save(private val context: Context) {

    public fun saveBitmapToGallery(bitmap: Bitmap, filename: String): Boolean {
        val directory = getAlbumStorageDir("Redactor")
        if (!directory.exists()) {
            if (!directory.mkdirs()) {
                return false
            }
        }
        val file = File(directory, filename)
        return try {
            FileOutputStream(file).use { out ->
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, out)
                MediaStore.Images.Media.insertImage(context.contentResolver, file.absolutePath, file.name, file.name)
            }
            showSavedMessage()
            true
        } catch (e: IOException) {
            e.printStackTrace()
            false
        }
    }

    private fun getAlbumStorageDir(albumName: String): File {
        val file = File(Environment.getExternalStoragePublicDirectory(
            Environment.DIRECTORY_PICTURES), albumName)
        if (!file.mkdirs()) {
        }
        return file
    }

    public fun savePicture(bitmap: Bitmap) {
        val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
        val fileName = "IMG_$timeStamp.jpg"
        if (saveBitmapToGallery(bitmap, fileName)) {
            showSavedMessage()
        }
    }

    private fun showSavedMessage() {
        Toast.makeText(context, "Фото сохранено", Toast.LENGTH_SHORT).show()
    }
}