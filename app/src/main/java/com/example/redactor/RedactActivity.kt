package com.example.redactor

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.drawable.BitmapDrawable
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.SeekBar
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.redactor.actions.Save
import com.example.redactor.algorithms.Filters
import com.example.redactor.algorithms.Rotate
import com.example.redactor.databinding.ActivityRedactBinding
import java.io.File
import java.io.FileOutputStream
import java.io.OutputStream

class RedactActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRedactBinding
    val rotate = Rotate()
    val filers = Filters();

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

        val rotation: Button = findViewById(R.id.rotate)
        rotation.setOnClickListener {
            seekOn();
            actionButtonsOn();
        }

        val retuchButton: Button = findViewById(R.id.retuch);

        retuchButton.setOnClickListener {
            seekBrushOn();
            actionButtonsOn();
        }

        val imageUri = intent.getParcelableExtra<Uri>(MainActivity.KEY_IMAGE_URI)
        imageUri?.let {
            val inputStream = contentResolver.openInputStream(it)
            val bitmap = BitmapFactory.decodeStream(inputStream)
            inputStream?.close()
            binding.imagePreview.setImageBitmap(bitmap)
            binding.imagePreview.visibility = View.VISIBLE
        }

        val downloadButton: ImageView = findViewById(R.id.download)
        downloadButton.setOnClickListener {
            val bitmap = (binding.imagePreview.drawable as BitmapDrawable).bitmap
            saveInstance.savePicture(bitmap)
        }

        val seekBar: SeekBar = findViewById(R.id.settingAngle)
        val seekBarBrush: SeekBar = findViewById(R.id.sizeBrush)
        val text: TextView = findViewById(R.id.currentAngle)

        seekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar, progress: Int, fromUser: Boolean) {
                text.text = progress.toString()
            }

            override fun onStartTrackingTouch(seekBar: SeekBar) { }

            override fun onStopTrackingTouch(seekBar: SeekBar) {
//                val rotatedBitmap = rotate.rotatePicture(
//                    (binding.imagePreview.drawable as BitmapDrawable).bitmap,
//                    seekBar.progress.toDouble()
//                )
//                binding.imagePreview.setImageBitmap(rotatedBitmap)
            }
        })

        seekBarBrush.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBarBrush: SeekBar, progress: Int, fromUser: Boolean) {
                text.text = progress.toString()
            }

            override fun onStartTrackingTouch(seekBarBrush: SeekBar) { }

            override fun onStopTrackingTouch(seekBarBrush: SeekBar) {

            }
        })

        val filterButton: Button = findViewById(R.id.filters);
        val firstFilter: Button = findViewById(R.id.button1);
        val secondFilter: Button = findViewById(R.id.button2);
        val thirdFilter: Button = findViewById(R.id.button3);
        val fourthFilter: Button = findViewById(R.id.button4);
        val fifthFilter: Button = findViewById(R.id.button5);
        val sixthFilter: Button = findViewById(R.id.button6);

        filterButton.setOnClickListener {
            btnFiltersOn();
            actionButtonsOn();
        }

        firstFilter.setOnClickListener {
            val red = filers.redImage((binding.imagePreview.drawable as BitmapDrawable).bitmap)
            binding.imagePreview.setImageBitmap(red)
        }

        secondFilter.setOnClickListener {
            val green = filers.greenImage((binding.imagePreview.drawable as BitmapDrawable).bitmap)
            binding.imagePreview.setImageBitmap(green)
        }

        thirdFilter.setOnClickListener {
            val blue = filers.blueImage((binding.imagePreview.drawable as BitmapDrawable).bitmap)
            binding.imagePreview.setImageBitmap(blue)
        }

        fourthFilter.setOnClickListener {
            val mosaic = filers.mosaicImage((binding.imagePreview.drawable as BitmapDrawable).bitmap)
            binding.imagePreview.setImageBitmap(mosaic)
        }

        fifthFilter.setOnClickListener {
            val mirror = filers.mirrorImage((binding.imagePreview.drawable as BitmapDrawable).bitmap)
            binding.imagePreview.setImageBitmap(mirror)
        }

        sixthFilter.setOnClickListener {
            val negative = filers.negativeImage((binding.imagePreview.drawable as BitmapDrawable).bitmap)
            binding.imagePreview.setImageBitmap(negative)
        }

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

    public fun seekBrushOn(){
        val seek: SeekBar = findViewById(R.id.sizeBrush)
        val testSetting: TextView = findViewById(R.id.currentAngle)
        seek.visibility = View.VISIBLE
        testSetting.visibility = View.VISIBLE
    }

    public fun btnFiltersOn(){
        val btn: LinearLayout = findViewById(R.id.filt);
        btn.visibility = View.VISIBLE;
    }

    public fun actionButtonsOn(){
        val firstActions: ImageView = findViewById(R.id.rollBack);
        val secondActions: ImageView = findViewById(R.id.success);
        firstActions.visibility = View.VISIBLE;
        secondActions.visibility = View.VISIBLE;
    }
}