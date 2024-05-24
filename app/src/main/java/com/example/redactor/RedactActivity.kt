package com.example.redactor

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.drawable.BitmapDrawable
import android.net.Uri
import android.os.Bundle
import android.view.MotionEvent
import android.view.View
import android.widget.ImageView
import android.widget.SeekBar
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatImageView
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.LinearSnapHelper
import androidx.recyclerview.widget.RecyclerView
import com.example.redactor.actions.Save
import com.example.redactor.algorithms.Filters
import com.example.redactor.algorithms.Retouching
import com.example.redactor.algorithms.Rotate
import com.example.redactor.algorithms.Scale
import com.example.redactor.algorithms.UnsharpMasking
import com.example.redactor.databinding.ActivityRedactBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class RedactActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRedactBinding
    val rotate = Rotate();
    val filers = Filters();
    val mask = UnsharpMasking();
    val retuch = Retouching();
    val scale = Scale();

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



        val listActions: List<ItemBlock> = listOf(
            ItemBlock(R.drawable.baseline_crop_rotate_24, "Поворот"),
            ItemBlock(R.drawable.baseline_filter_24, "Фильтры"),
            ItemBlock(R.drawable.baseline_add_24, "Маскировка"),
            ItemBlock(R.drawable.baseline_face_retouching_natural_24, "Ретушь"),
            ItemBlock(R.drawable.baseline_face_retouching_natural_24, "Масштаб"),

        )

        val firstSeekBar: SeekBar = findViewById(R.id.seekBar1)
        val firstText: TextView = findViewById(R.id.firstText)
        val secondSeekBar: SeekBar = findViewById(R.id.seekBar2)
        val secondText: TextView = findViewById(R.id.secondText)

        val carousel : RecyclerView = findViewById(R.id.recycler)

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
                        recycerOff()
                        seekOffFirstBar()
                        seekOffSecondBar()
                        seekOnFirstBar()
                        seekBarRotate(firstSeekBar, firstText)
                    }
                    1->{
                        seekOffFirstBar()
                        seekOffSecondBar()
                        recyclerOn()
                    }
                    2->{
                        recycerOff()
                        seekOffFirstBar()
                        seekOffSecondBar()
                        seekOnSecondBar()
                    }
                    3->{
                        recycerOff()
                        seekOffFirstBar()
                        seekOffSecondBar()
                        seekOnFirstBar()
                        seekOnSecondBar()
                        seekBarRetouch(firstSeekBar, firstText, secondSeekBar, secondText)
                    }
                    4->{
                        recycerOff()
                        seekOffFirstBar()
                        seekOffSecondBar()
                        seekOnFirstBar()
                        seekBarScale(firstSeekBar, firstText)
                    }
                }
            }
        }

        val listFilters : List<ItemBlock> = listOf(
            ItemBlock(R.drawable.red_filter, "Красный"),
            ItemBlock(R.drawable.green_filter, "Зеленый"),
            ItemBlock(R.drawable.blue_filter, "Синий"),
            ItemBlock(R.drawable.mosaic_filter, "Мозаика"),
            ItemBlock(R.drawable.mirroring_filter, "отзеркал."),
            ItemBlock(R.drawable.negative_filter, "Негатив")
        )

        val filterCarousel : RecyclerView = findViewById(R.id.filterRecycler)

//        ggg

        val filterAdapter = AlgorithmsAdapter(listFilters, this)
        filterCarousel.adapter = filterAdapter

//        val layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        filterCarousel.layoutManager = layoutManager
//        layoutManager.scrollToPositionWithOffset(0, resources.displayMetrics.widthPixels / 2)

//        val snapHelperr = LinearSnapHelper()
        snapHelper.attachToRecyclerView(filterCarousel)

//        filterAdapter.listner = object: FiltersAdapter.OnItemClickListener{
//            override fun onItemClick(position: Int, item: Int) {
//                when (position){
//                    0->{
//                        val red = filers.redImage((binding.imagePreview.drawable as BitmapDrawable).bitmap)
//                        binding.imagePreview.setImageBitmap(red)
//                    }
//                    1->{
//                        val green = filers.greenImage((binding.imagePreview.drawable as BitmapDrawable).bitmap)
//                        binding.imagePreview.setImageBitmap(green)
//                    }
//                    2->{
//                        val blue = filers.blueImage((binding.imagePreview.drawable as BitmapDrawable).bitmap)
//                        binding.imagePreview.setImageBitmap(blue)
//                    }
//                    3->{
//                        val mosaic = filers.mosaicImage((binding.imagePreview.drawable as BitmapDrawable).bitmap)
//                        binding.imagePreview.setImageBitmap(mosaic)
//                    }
//                    4->{
//                        val mirror = filers.mirrorImage((binding.imagePreview.drawable as BitmapDrawable).bitmap)
//                        binding.imagePreview.setImageBitmap(mirror)
//                    }
//                    5->{
//                        val negative = filers.negativeImage((binding.imagePreview.drawable as BitmapDrawable).bitmap)
//                        binding.imagePreview.setImageBitmap(negative)
//                    }
//                }
//            }
//        }

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
                    val originalPhoto = (binding.imagePreview.drawable as BitmapDrawable).bitmap;
                    val rotatedBitmap = rotate.rotatePicture(
                        originalPhoto,
                    seekBar.progress.toDouble()
                    )
                    binding.imagePreview.setImageBitmap(rotatedBitmap);
                }
            }
        })
    }

    @SuppressLint("ClickableViewAccessibility")
    public fun seekBarRetouch(firstSeekBar: SeekBar, firstText: TextView, secondSeekBar: SeekBar, secondText: TextView) {
        firstSeekBar.min = 0
        firstSeekBar.max = 50
        secondSeekBar.min = 0
        secondSeekBar.max = 50

        firstSeekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(firstSeekBar: SeekBar, progress: Int, fromUser: Boolean) {
                firstText.text = "Radius: ${progress}"
            }

            override fun onStartTrackingTouch(firstSeekBar: SeekBar) {}

            override fun onStopTrackingTouch(firstSeekBar: SeekBar) {}
        })

        secondSeekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(secondSeekBar: SeekBar, progress: Int, fromUser: Boolean) {
                secondText.text = "Ratio: ${progress}"
            }

            override fun onStartTrackingTouch(secondSeekBar: SeekBar) {}

            override fun onStopTrackingTouch(secondSeekBar: SeekBar) {}
        })

        binding.imagePreview.setOnTouchListener { v, event ->
            when (event.action) {
                MotionEvent.ACTION_MOVE -> {
                    val imageView = v as ImageView
                    val drawable = imageView.drawable as? BitmapDrawable
                    drawable?.bitmap?.let { bitmap ->
                        // Ensure the bitmap is mutable
                        val mutableBitmap = bitmap.copy(Bitmap.Config.ARGB_8888, true)

                        val x = event.x
                        val y = event.y

                        val ratioX = mutableBitmap.width / imageView.width.toFloat()
                        val ratioY = mutableBitmap.height / imageView.height.toFloat()

                        // Adjust for touch inaccuracies by considering the touch area relative to the bitmap
                        val touchX = (x * ratioX).toInt().coerceIn(0, mutableBitmap.width - 1)
                        val touchY = (y * ratioY).toInt().coerceIn(0, mutableBitmap.height - 1)

                        lifecycleScope.launch {
                            val radius = firstSeekBar.progress.toDouble()
                            val ratioRetouch = secondSeekBar.progress

                            // Perform the retouch
                            val retouchedBitmap = withContext(Dispatchers.Default) {
                                retuch.retush(mutableBitmap, touchX.toDouble(), touchY.toDouble(), radius, ratioRetouch)
                            }

                            // Update the ImageView on the main thread
                            withContext(Dispatchers.Main) {
                                binding.imagePreview.setImageBitmap(retouchedBitmap)
                            }
                        }
                    }
                }
            }
            true
        }
    }


    public fun seekBarUnsharpMasking(firstSeekBar: SeekBar, firstText: TextView, secondSeekBar: SeekBar, secondText: TextView){
        firstSeekBar.min = 2;
        firstSeekBar.max = 15;
        secondSeekBar.min = 1;
        secondSeekBar.max = 15;

        firstSeekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(firstSeekBar: SeekBar, progress: Int, fromUser: Boolean) {
                firstText.text = "Radius: ${progress.toString()}"
            }

            override fun onStartTrackingTouch(firstSeekBar: SeekBar) { }

            override fun onStopTrackingTouch(firstSeekBar: SeekBar) { }
        })

        secondSeekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(secondSeekBar: SeekBar, progress: Int, fromUser: Boolean) {
                secondText.text = "Degree: ${progress.toString()}"
            }

            override fun onStartTrackingTouch(secondSeekBar: SeekBar) { }

            override fun onStopTrackingTouch(secondSeekBar: SeekBar) { }
        })
    }

    public fun seekBarScale(firstSeekBar: SeekBar, firstText: TextView){
        firstSeekBar.min = 0
        firstSeekBar.max = 20;

        firstSeekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(firstSeekBar: SeekBar, progress: Int, fromUser: Boolean) {
                firstText.text = "Ratio: ${progress.toString()}"
            }

            override fun onStartTrackingTouch(firstSeekBar: SeekBar) { }

            override fun onStopTrackingTouch(firstSeekBar: SeekBar) {
                lifecycleScope.launch {
                    val originalPhoto = (binding.imagePreview.drawable as BitmapDrawable).bitmap;
                    val scaleBitmap = scale.imageScalling(
                        originalPhoto,
                        firstSeekBar.progress.toDouble()
                    )
                    binding.imagePreview.setImageBitmap(scaleBitmap);
                }
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
        val testSetting: TextView = findViewById(R.id.firstText)
        seek.visibility = View.VISIBLE
        testSetting.visibility = View.VISIBLE
    }

    public fun seekOffFirstBar(){
        val seek: SeekBar = findViewById(R.id.seekBar1)
        val textSetting: TextView = findViewById(R.id.firstText)
        seek.visibility = View.INVISIBLE
        seek.progress = seek.min
        textSetting.text = ""
        textSetting.visibility = View.INVISIBLE
    }

    public fun seekOnSecondBar() {
        val seek: SeekBar = findViewById(R.id.seekBar2)
        val testSetting: TextView = findViewById(R.id.secondText)
        seek.visibility = View.VISIBLE
        testSetting.visibility = View.VISIBLE
    }

    public fun seekOffSecondBar(){
        val seek: SeekBar = findViewById(R.id.seekBar2)
        val textSetting: TextView = findViewById(R.id.secondText)
        seek.visibility = View.INVISIBLE
        seek.progress = seek.min
        textSetting.text = ""
        textSetting.visibility = View.INVISIBLE
    }

    public fun recyclerOn(){
        val recyc: RecyclerView = findViewById(R.id.filterRecycler)
        recyc.visibility = View.VISIBLE
    }

    public fun recycerOff(){
        val recyc: RecyclerView = findViewById(R.id.filterRecycler)
        recyc.visibility = View.INVISIBLE
    }
}