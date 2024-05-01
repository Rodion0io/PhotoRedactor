package com.example.redactor

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.View
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
    private val PICK_IMAGE_REQUEST = 1
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

        val check: View = findViewById(R.id.firstBlock)

        check.setOnClickListener{
            Toast.makeText(this, "bimbimbambam", Toast.LENGTH_SHORT).show();
        }


        val nextt: View = findViewById(R.id.secondBlock);

        nextt.setOnClickListener {
            onClick();
        }

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

    public fun onClick(){
        val action = Intent(this, RedactActivity::class.java);
        startActivity(action);
    }


}