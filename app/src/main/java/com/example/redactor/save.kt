//package com.example.redactor
//
//import android.content.ContentValues
//import android.content.Context
//import android.content.Intent
//import android.graphics.Bitmap
//import android.graphics.drawable.BitmapDrawable
//import android.net.Uri
//import android.provider.MediaStore
//import android.widget.ImageView
//
//fun saveImageToGallery(imageView: ImageView, context: Context) {
//    val bitmap = (imageView.drawable as BitmapDrawable).bitmap
//    val contentResolver = context.contentResolver
//
//    val values = ContentValues().apply {
//        put(MediaStore.Images.Media.DISPLAY_NAME, "my_image.jpg")
//        put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg")
//    }
//
//    val imageUri = contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values)
//
//    try {
//        contentResolver.openOutputStream(imageUri!!).use { outputStream ->
//            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream)
//        }
//    } catch (e: Exception) {
//        e.printStackTrace()
//    }
//
//    // Открываем галерею для просмотра нового изображения
//    val intent = Intent(Intent.ACTION_VIEW).apply {
//        setDataAndType(imageUri, "image/*")
//        addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
//    }
//    context.startActivity(intent)
//}