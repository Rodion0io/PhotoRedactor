package com.example.redactor

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.drawable.BitmapDrawable
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.SeekBar
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.RecyclerView
import com.example.redactor.actions.Save
import com.example.redactor.algorithms.Rotate
import com.example.redactor.databinding.ActivityRedactBinding
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import java.io.File
import java.io.FileOutputStream
import java.io.OutputStream
import java.util.logging.Filter

class RedactActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRedactBinding
    val rotate = Rotate()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRedactBinding.inflate(layoutInflater)
        enableEdgeToEdge()
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val saveInstance = Save(this)

        val backButton: View = findViewById(R.id.back)
        backButton.setOnClickListener { onClick() }

        setListners()

        val a: Button = findViewById(R.id.rotate)
        a.setOnClickListener { seekOn() }

        val imageUri = intent.getParcelableExtra<Uri>(MainActivity.KEY_IMAGE_URI)
        imageUri?.let {
            val inputStream = contentResolver.openInputStream(it)
            val bitmap = BitmapFactory.decodeStream(inputStream)
            inputStream?.close()


            binding.imagePreview.scaleType = ImageView.ScaleType.FIT_CENTER

            binding.imagePreview.setImageBitmap(bitmap)
            binding.imagePreview.visibility = View.VISIBLE
        }

        val downloadButton: ImageView = findViewById(R.id.download)
        downloadButton.setOnClickListener {
            val bitmap = (binding.imagePreview.drawable as BitmapDrawable).bitmap
            saveInstance.savePicture(bitmap)
        }

        val seekBar: SeekBar = findViewById(R.id.settingAngle)
        val text: TextView = findViewById(R.id.currentAngle)

//        seekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
//            override fun onProgressChanged(seekBar: SeekBar, progress: Int, fromUser: Boolean) {
//                text.text = progress.toString()
//            }
//
//            override fun onStartTrackingTouch(seekBar: SeekBar) { }
//
//            override fun onStopTrackingTouch(seekBar: SeekBar) {
//                suspend fun rotation(degrees: Double, bitmap: Bitmap) {
//                    val rotatedBitmap =
//                        Rotate.rotatePicture(bitmap, degrees)
//                    imageView.setImageBitmap(rotatedBitmap)
//                    rotationBar.isEnabled = true
//                }
//
//
//                lifecycleScope.launch {
//                    rotation(rotationBar.progress.toDouble(), bitmap)
//                }
//                val rotatedBitmap = rotate.rotatePicture(
//                    (binding.imagePreview.drawable as BitmapDrawable).bitmap,
//                    seekBar.progress.toDouble()
//                )
//                binding.imagePreview.setImageBitmap(rotatedBitmap)
            }

        })

    }



    private fun setListners() {
        binding.back.setOnClickListener { onBackPressed() }
    }

    public fun onClick() {
        val action = Intent(this, MainActivity::class.java)
        startActivity(action)
    }

    public fun seekOn() {
        val seek: SeekBar = findViewById(R.id.settingAngle)
        val testSetting: TextView = findViewById(R.id.currentAngle)
        seek.visibility = View.VISIBLE
        testSetting.visibility = View.VISIBLE
    }
}