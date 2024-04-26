package com.example.redactor

import android.Manifest
import android.app.Instrumentation.ActivityResult
import android.content.pm.PackageManager
import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class MainActivity : AppCompatActivity() {

    private lateinit var pLauncher: ActivityResultLauncher<String>
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        registerPermissionListner()
        checkCameraPermission()
    }
    private fun checkCameraPermission(){
        when {
            ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED -> {
                Toast.makeText(this, "Camera run", Toast.LENGTH_LONG).show()
            }
            else ->{
                pLauncher.launch(Manifest.permission.CAMERA)
            }
        }
    }

    private fun registerPermissionListner(){
        pLauncher = registerForActivityResult(ActivityResultContracts.RequestPermission()){
            if (it){
                Toast.makeText(this, "Camera run", Toast.LENGTH_LONG).show()
            }
            else{
                Toast.makeText(this, "Permission denied", Toast.LENGTH_LONG).show()
            }
        }
    }
}