package com.example.redactor

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.drawable.BitmapDrawable
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.SeekBar
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.redactor.actions.Save
import com.example.redactor.algorithms.Rotate
import com.example.redactor.databinding.ActivityRedactBinding
import java.io.File
import java.io.FileOutputStream
import java.io.OutputStream


class RedactActivity : AppCompatActivity() {


    private lateinit var binding: ActivityRedactBinding
    val rotate = Rotate();

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

        val saveInstance = Save(this);

        val backButton: View = findViewById(R.id.back);

        backButton.setOnClickListener {
            onClick();
        }

        onCreate()
        setListners()

        val a: Button = findViewById(R.id.knopka)

        a.setOnClickListener {
            
            seekOn()

        }

        var drawable = findViewById<ImageView>(R.id.imagePreview).drawable as BitmapDrawable;
        var bitmap = drawable.bitmap

        val downloadButton: ImageView = findViewById(R.id.download)

        downloadButton.setOnClickListener {
            saveInstance.savePicture(bitmap)
        }




        val seekBar: SeekBar = findViewById(R.id.settingAngle);
        val text: TextView = findViewById(R.id.currentAngle)

        seekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar, progress: Int, fromUser: Boolean) {
                text.text = progress.toString()
            }

            override fun onStartTrackingTouch(seekBar: SeekBar) {

            }

            override fun onStopTrackingTouch(seekBar: SeekBar) {
                val rotatedBitmap = rotate.rotatePicture(bitmap, seekBar.progress.toDouble())
                val imageView = findViewById<ImageView>(R.id.imagePreview)
                imageView.setImageBitmap(rotatedBitmap)
            }
        })


    }



    private fun onCreate(){
        intent.getParcelableExtra<Uri>(MainActivity.KEY_IMAGE_URI)?.let { imageUri ->
            val inputStream = contentResolver.openInputStream(imageUri)
            val bitmap = BitmapFactory.decodeStream(inputStream)
            inputStream?.close()
            binding.imagePreview.setImageBitmap(bitmap)
            binding.imagePreview.visibility = View.VISIBLE
        }
    }

    private fun setListners(){
        binding.back.setOnClickListener{
            onBackPressed();
        }
    }

    public fun onClick() {
        val action = Intent(this, MainActivity::class.java)
        startActivity(action)
    }

    public fun seekOn(){
        val seek: SeekBar = findViewById(R.id.settingAngle)
        seek.setVisibility(ConstraintLayout.VISIBLE)
    }

    private fun saveImageAndShare() {
        // Получаем Bitmap из ImageView
        val imageView = findViewById<ImageView>(R.id.imagePreview)
        val bitmap = (imageView.drawable as BitmapDrawable).bitmap

        // Сохраняем Bitmap во внешнем хранилище устройства
        val imagesDir =
            Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES)
        val imageFile = File(imagesDir, "image.png")

        val outputStream: OutputStream = FileOutputStream(imageFile)
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream)
        outputStream.flush()
        outputStream.close()

        // Создаем Intent для отправки изображения
        val shareIntent = Intent(Intent.ACTION_SEND)
        shareIntent.type = "image/png"
        shareIntent.putExtra(Intent.EXTRA_STREAM, imageFile)

        // Запускаем Intent
        startActivity(Intent.createChooser(shareIntent, "Share Image"))
    }
}