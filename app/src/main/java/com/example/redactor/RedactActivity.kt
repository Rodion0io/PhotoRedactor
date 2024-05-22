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
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.SeekBar
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.LinearSnapHelper
import androidx.recyclerview.widget.RecyclerView
import com.example.redactor.actions.Save
import com.example.redactor.algorithms.Filters
import com.example.redactor.algorithms.Masking
import com.example.redactor.algorithms.Rotate
import com.example.redactor.databinding.ActivityRedactBinding
import kotlinx.coroutines.launch
import org.w3c.dom.Text
import java.io.File
import java.io.FileOutputStream
import java.io.OutputStream


class RedactActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRedactBinding
    val rotate = Rotate();
    val filers = Filters();
    val ma = Masking();

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
        val newBitmap: Bitmap;

        val backButton: View = findViewById(R.id.back)
        backButton.setOnClickListener { onClick() }

        setListners()

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

        val seekBar: SeekBar = findViewById(R.id.seekBar1)
        val text: TextView = findViewById(R.id.currentAngle)


//        seekBarBrush.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
//            override fun onProgressChanged(seekBarBrush: SeekBar, progress: Int, fromUser: Boolean) {
//                text.text = progress.toString()
//            }
//
//            override fun onStartTrackingTouch(seekBarBrush: SeekBar) { }
//
//            override fun onStopTrackingTouch(seekBarBrush: SeekBar) {
//
//            }
//        })

//        val filterButton: Button = findViewById(R.id.filters);
//        val firstFilter: Button = findViewById(R.id.button1);
//        val secondFilter: Button = findViewById(R.id.button2);
//        val thirdFilter: Button = findViewById(R.id.button3);
//        val fourthFilter: Button = findViewById(R.id.button4);
//        val fifthFilter: Button = findViewById(R.id.button5);
//        val sixthFilter: Button = findViewById(R.id.button6);
//        val maska: Button = findViewById(R.id.mask)

//        maska.setOnClickListener {
//            val result = ma.Dr((binding.imagePreview.drawable as BitmapDrawable).bitmap, 5);
//            binding.imagePreview.setImageBitmap(result)
//        }

//        filterButton.setOnClickListener {
//            btnFiltersOn();
//            actionButtonsOn();
//        }

//        firstFilter.setOnClickListener {
//            val red = filers.redImage((binding.imagePreview.drawable as BitmapDrawable).bitmap)
//            binding.imagePreview.setImageBitmap(red)
//        }
//
//        secondFilter.setOnClickListener {
//            val green = filers.greenImage((binding.imagePreview.drawable as BitmapDrawable).bitmap)
//            binding.imagePreview.setImageBitmap(green)
//        }
//
//        thirdFilter.setOnClickListener {
//            val blue = filers.blueImage((binding.imagePreview.drawable as BitmapDrawable).bitmap)
//            binding.imagePreview.setImageBitmap(blue)
//        }
//
//        fourthFilter.setOnClickListener {
//            val mosaic = filers.mosaicImage((binding.imagePreview.drawable as BitmapDrawable).bitmap)
//            binding.imagePreview.setImageBitmap(mosaic)
//        }
//
//        fifthFilter.setOnClickListener {
//            val mirror = filers.mirrorImage((binding.imagePreview.drawable as BitmapDrawable).bitmap)
//            binding.imagePreview.setImageBitmap(mirror)
//        }
//
//        sixthFilter.setOnClickListener {
//            val negative = filers.negativeImage((binding.imagePreview.drawable as BitmapDrawable).bitmap)
//            binding.imagePreview.setImageBitmap(negative)
//        }

        val carousel : RecyclerView = findViewById(R.id.recycler)

        val listActions: List<ItemBlock> = listOf(
            ItemBlock(R.drawable.baseline_crop_rotate_24, "Поворот"),
            ItemBlock(R.drawable.baseline_filter_24, "Фильтры"),
            ItemBlock(R.drawable.baseline_add_24, "Маскировка"),
            ItemBlock(R.drawable.baseline_face_retouching_natural_24, "Ретушь")
        )



        val adapter = AlgorithmsAdapter(listActions, this)
        carousel.adapter = adapter

        val layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        carousel.layoutManager = layoutManager
        layoutManager.scrollToPositionWithOffset(0, resources.displayMetrics.widthPixels / 2)

        val snapHelper = LinearSnapHelper()
        snapHelper.attachToRecyclerView(carousel)


        adapter.listner = object: AlgorithmsAdapter.OnItemClickListener{
            override fun onItemClick(position: Int, item: Int) {
                when (position){
                    0->{
                        seekOnFirstBar()
                        seekBarRotate(seekBar, text)
                    }
                    1->{

                    }
                }
            }
        }

    }

    public fun seekBarRotate(seekBar: SeekBar, text: TextView){
        seekBar.min = -180;
        seekBar.max = 180;
        seekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar, progress: Int, fromUser: Boolean) {
                text.text = progress.toString()
            }

            override fun onStartTrackingTouch(seekBar: SeekBar) { }

            override fun onStopTrackingTouch(seekBar: SeekBar) {
                lifecycleScope.launch {
                    val rotatedBitmap = rotate.rotatePicture(
                        (binding.imagePreview.drawable as BitmapDrawable).bitmap,
                    seekBar.progress.toDouble()
                    )
                    binding.imagePreview.setImageBitmap(rotatedBitmap)
                }
            }
        })
    }

    public fun seekBarRetuch(firstSeekBar: SeekBar, firstText: TextView, secondSeekBar: SeekBar, secondText: TextView){
        firstSeekBar.min = 0;
        firstSeekBar.max = 50;
        secondSeekBar.min = 0;
        secondSeekBar.max = 50

        firstSeekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(firstSeekBar: SeekBar, progress: Int, fromUser: Boolean) {
                firstText.text = progress.toString()
            }

            override fun onStartTrackingTouch(firstSeekBar: SeekBar) { }

            override fun onStopTrackingTouch(firstSeekBar: SeekBar) { }
        })

        secondSeekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(secondSeekBar: SeekBar, progress: Int, fromUser: Boolean) {
                secondText.text = progress.toString()
            }

            override fun onStartTrackingTouch(secondSeekBar: SeekBar) { }

            override fun onStopTrackingTouch(secondSeekBar: SeekBar) {
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

    public fun seekOnFirstBar() {
        val seek: SeekBar = findViewById(R.id.seekBar1)
        val testSetting: TextView = findViewById(R.id.currentAngle)
        seek.visibility = View.VISIBLE
        testSetting.visibility = View.VISIBLE
    }

    public fun seekOffFirstBar(){
        val seek: SeekBar = findViewById(R.id.seekBar1)
        val testSetting: TextView = findViewById(R.id.currentAngle)
        seek.visibility = View.INVISIBLE
        testSetting.visibility = View.INVISIBLE
    }

    public fun seekOnSecondBar() {
        val seek: SeekBar = findViewById(R.id.seekBar2)
        val testSetting: TextView = findViewById(R.id.currentAngle)
        seek.visibility = View.VISIBLE
        testSetting.visibility = View.VISIBLE
    }

    public fun seekOffSecondBar(){
        val seek: SeekBar = findViewById(R.id.seekBar2)
        val testSetting: TextView = findViewById(R.id.currentAngle)
        seek.visibility = View.INVISIBLE
        testSetting.visibility = View.INVISIBLE
    }

}