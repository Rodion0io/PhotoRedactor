//package com.example.redactor.actions
//
//import android.Manifest
//import android.content.pm.PackageManager
//import android.widget.Toast
//import androidx.activity.result.contract.ActivityResultContracts
//import androidx.core.content.ContextCompat
//import com.example.redactor.databinding.ActivityRedactBinding
//
//class permissions {
//
//    private lateinit var binding: ActivityRedactBinding
//    public fun checkCameraPermission(){
//        when {
//            ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED -> {
//                Toast.makeText(this, "Camera run", Toast.LENGTH_LONG).show()
//            }
//            else ->{
//                pLauncher.launch(Manifest.permission.CAMERA)
//            }
//        }
//    }
//
//    public fun registerPermissionListner(){
//        pLauncher = registerForActivityResult(ActivityResultContracts.RequestPermission()){
//            if (it){
//                Toast.makeText(this, "Camera run", Toast.LENGTH_LONG).show()
//            }
//            else{
//                Toast.makeText(this, "Permission denied", Toast.LENGTH_LONG).show()
//            }
//        }
//    }
//}