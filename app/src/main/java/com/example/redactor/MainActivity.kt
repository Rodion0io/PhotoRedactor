package com.example.redactor

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.provider.Settings
import android.view.View
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.redactor.databinding.ActivityMainBinding
import java.io.File

class MainActivity : AppCompatActivity() {

    companion object {
        private const val REQUEST_CODE_PICK_IMAGE = 1
        private const val REQUEST_IMAGE_CAPTURE = 2
        private const val FILENAME_TEMP = "temp_image.jpg"
        private const val FILE_PROVIDER_AUTHORITY = "com.example.redactor.fileprovider"
        const val KEY_IMAGE_URI = "imageUri"
        const val KEY_IMAGE_BITMAP = "imageBitmap"
    }

    private lateinit var binding: ActivityMainBinding
    private lateinit var cameraPermissionLauncher: ActivityResultLauncher<String>
    private lateinit var storagePermissionLauncher: ActivityResultLauncher<String>
    private lateinit var imageTempUri: Uri

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, 0, systemBars.right, 0)
            insets
        }

        registerPermissionLaunchers()

        imageTempUri = FileProvider.getUriForFile(
            this,
            FILE_PROVIDER_AUTHORITY,
            File(cacheDir, FILENAME_TEMP)
        )

        binding.secondBlock.setOnClickListener { takePictIntent() }
        binding.firstBlock.setOnClickListener { openGallery() }
        binding.toSpineButton.setOnClickListener {
            val action = Intent(this, SplineActivity::class.java)
            startActivity(action)
        }
    }

    private fun openGallery() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
            Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI).also { pickerIntent ->
                pickerIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                startActivityForResult(pickerIntent, REQUEST_CODE_PICK_IMAGE)
            }
        } else {
            storagePermissionLauncher.launch(Manifest.permission.READ_EXTERNAL_STORAGE)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when (requestCode) {
            REQUEST_CODE_PICK_IMAGE -> {
                if (resultCode == Activity.RESULT_OK && data != null) {
                    data.data?.let { imageUri ->
                        val intent = Intent(this, RedactActivity::class.java)
                        intent.putExtra(KEY_IMAGE_URI, imageUri)
                        startActivity(intent)
                    }
                }
            }
            REQUEST_IMAGE_CAPTURE -> {
                if (resultCode == Activity.RESULT_OK) {
                    val intent = Intent(this, RedactActivity::class.java)
                    intent.putExtra(KEY_IMAGE_URI, imageTempUri)
                    startActivity(intent)
                }
            }
        }
    }

    private fun registerPermissionLaunchers() {
        cameraPermissionLauncher = registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
            if (isGranted) {
                takePictIntent()
            } else {
                Toast.makeText(this, "Camera permission denied. Redirecting to settings...", Toast.LENGTH_SHORT).show()
                openAppPermissionSettings()
            }
        }

        storagePermissionLauncher = registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
            if (isGranted) {
                openGallery()
            } else {
                Toast.makeText(this, "Storage permission denied. Redirecting to settings...", Toast.LENGTH_SHORT).show()
                openAppPermissionSettings()
            }
        }
    }

    private fun takePictIntent() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
            Intent(MediaStore.ACTION_IMAGE_CAPTURE).also { takePictureIntent ->
                takePictureIntent.resolveActivity(packageManager)?.also {
                    takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, imageTempUri)
                    startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE)
                }
            }
        } else {
            cameraPermissionLauncher.launch(Manifest.permission.CAMERA)
        }
    }

    private fun openAppPermissionSettings() {
        val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
            data = Uri.fromParts("package", packageName, null)
        }
        startActivity(intent)
    }
}
