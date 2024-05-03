package com.example.redactor

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.provider.MediaStore
import android.view.View
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.redactor.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    companion object{
        private const val REQUEST_CODE_PICK_IMAGE = 1;
        const val KEY_IMAGE_URI = "imageUri"
    }

    private lateinit var binding: ActivityMainBinding;

    private lateinit var pLauncher: ActivityResultLauncher<String>
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater);
        enableEdgeToEdge()
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }


        registerPermissionListner()
        checkCameraPermission()

//        val check: View = findViewById(R.id.firstBlock)
//
//        check.setOnClickListener{
//            Toast.makeText(this, "bimbimbambam", Toast.LENGTH_SHORT).show();
//        }


        val nextt: View = findViewById(R.id.secondBlock);

        nextt.setOnClickListener {
            onClick();
        }
        setListners()
    }

    private fun setListners(){
        binding.firstBlock.setOnClickListener{
            Intent(
                Intent.ACTION_PICK,
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI
            ).also { pickerIntent ->
                pickerIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                startActivityForResult(pickerIntent, REQUEST_CODE_PICK_IMAGE)
            }
        }
    }
//
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_CODE_PICK_IMAGE && resultCode == RESULT_OK && data != null) {
            data.data?.let { imageUri ->
                Intent(applicationContext, RedactActivity::class.java).also { RedactActivity ->
                    RedactActivity.putExtra(KEY_IMAGE_URI, imageUri)
                    startActivity(RedactActivity)
                }
            }
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

    public fun onClick() {
        val action = Intent(this, RedactActivity::class.java)
        startActivity(action)
    }


}