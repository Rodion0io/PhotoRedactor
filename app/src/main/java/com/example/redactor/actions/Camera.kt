//package com.example.redactor.actions
//
//import android.icu.text.SimpleDateFormat
//import android.os.Environment
//import android.provider.MediaStore
//import androidx.core.app.ActivityCompat.startActivityForResult
//import androidx.core.content.FileProvider
//import java.io.IOException
//import java.sql.Date
//import java.util.Locale
//
//
//class Camera<File> {
//    private val REQUEST_IMAGE_CAPTURE = 1
//    private lateinit var photoURI: Uri
//
//    private fun dispatchTakePictureIntent() {
//        Intent(MediaStore.ACTION_IMAGE_CAPTURE).also { takePictureIntent ->
//
//            val packageManager
//            takePictureIntent.resolveActivity(packageManager)?.also {
//                // Create the File where the photo should go
//                val photoFile: File? = try {
//                    createImageFile()
//                } catch (ex: IOException) {
//                    // Error occurred while creating the File
//                    null
//                }
//                // Continue only if the File was successfully created
//                photoFile?.also {
//                    photoURI = FileProvider.getUriForFile(
//                        this,
//                        "com.example.android.fileprovider",
//                        it
//                    )
//                    takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI)
//                    startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE)
//                }
//            }
//        }
//    }
//
//    private fun createImageFile(): File {
//        // Create an image file name
//        val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(java.util.Date())
//        val imageFileName = "JPEG_" + timeStamp + "_"
//        val storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES)
//        return File.createTempFile(
//            imageFileName,  /* prefix */
//            ".jpg",         /* suffix */
//            storageDir      /* directory */
//        )
//    }
//
//
//}