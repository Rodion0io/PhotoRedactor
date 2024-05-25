package com.example.redactor

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.graphics.drawable.BitmapDrawable
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.view.MotionEvent
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.SeekBar
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.annotation.RequiresApi
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
import com.example.redactor.algorithms.CryptText
import com.example.redactor.algorithms.DecryptText
class RedactActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRedactBinding
    val rotate = Rotate();
    val filers = Filters();
    val mask = UnsharpMasking();
    val retuch = Retouching();
    val scale = Scale();
    val CryptText = CryptText();
    val DecryptText = DecryptText()

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
        val firstSeekBar: SeekBar = findViewById(R.id.seekBar1)
        val firstText: TextView = findViewById(R.id.firstText)
        val secondSeekBar: SeekBar = findViewById(R.id.seekBar2)
        val secondText: TextView = findViewById(R.id.secondText)


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
            ItemBlock(R.drawable.baseline_retush_24, "Ретушь"),
            ItemBlock(R.drawable.baseline_face_retouching_natural_24, "Масштаб"),
            ItemBlock(R.drawable.baseline_grid_4x4_24, "Шифр")

            )

        val listFilters: List<ItemBlock> = listOf(
            ItemBlock(R.drawable.red_filter, "Красный"),
            ItemBlock(R.drawable.green_filter, "Зеленый"),
            ItemBlock(R.drawable.blue_filter, "Синий"),
            ItemBlock(R.drawable.mosaic_filter, "Мозаика"),
            ItemBlock(R.drawable.mirroring_filter, "отзеркал."),
            ItemBlock(R.drawable.negative_filter, "Негатив")

        )

        val carousel: RecyclerView = findViewById(R.id.recycler)

        val adapter = AlgorithmsAdapter(listActions, this)
        carousel.adapter = adapter

        val layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        carousel.layoutManager = layoutManager
        layoutManager.scrollToPositionWithOffset(0, resources.displayMetrics.widthPixels / 2)

        val snapHelper = LinearSnapHelper()
        snapHelper.attachToRecyclerView(carousel)


        adapter.listner = object : AlgorithmsAdapter.OnItemClickListener {
            override fun onItemClick(position: Int, item: Int) {
                when (position) {
                    0 -> {
                        btnOfUnsharp()
                        recycerOff()
                        seekOffFirstBar()
                        seekOffSecondBar()
                        seekOffCrypt()
                        seekOnFirstBar()
                        seekBarRotate(firstSeekBar, firstText)
                    }

                    1 -> {
                        btnOfUnsharp()
                        seekOffFirstBar()
                        seekOffCrypt()
                        seekOffSecondBar()
                        recyclerOn()
                    }

                    2 -> {
                        btnOnUnsharp()
                        recycerOff()
                        seekOffFirstBar()
                        seekOffCrypt()
                        seekOffSecondBar()
                        seekOnSecondBar()
                    }

                    3 -> {
                        btnOfUnsharp()
                        recycerOff()
                        seekOffFirstBar()
                        seekOffSecondBar()
                        seekOffCrypt()
                        seekOnFirstBar()
                        seekOnSecondBar()
                        seekBarRetouch(firstSeekBar, firstText, secondSeekBar, secondText)
                    }

                    4 -> {
                        btnOfUnsharp()
                        recycerOff()
                        seekOffFirstBar()
                        seekOffSecondBar()
                        seekOffCrypt()
                        seekOnFirstBar()
                        seekBarScale(firstSeekBar, firstText)
                    }
                    5->{
                        btnOfUnsharp()
                        recycerOff()
                        seekOffFirstBar()
                        seekOffSecondBar()
                        seekOnCrypt()
                    }
                }
            }
        }


        val filterCarousel: RecyclerView = findViewById(R.id.filterRecycler)

        val filterAdapter = AlgorithmsAdapter(listFilters, this)
        filterCarousel.adapter = filterAdapter

        val layoutManagerr = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        filterCarousel.layoutManager = layoutManagerr
        layoutManagerr.scrollToPositionWithOffset(0, resources.displayMetrics.widthPixels / 2)

        val snapHelperr = LinearSnapHelper()
        snapHelperr.attachToRecyclerView(filterCarousel)

        filterAdapter.listner = object : FiltersAdapter.OnItemClickListener,
            AlgorithmsAdapter.OnItemClickListener {
            override fun onItemClick(position: Int, item: Int) {
                when (position) {
                    0 -> {
                        val originalPhoto = (binding.imagePreview.drawable as BitmapDrawable).bitmap;
                        val red =
                            filers.redImage(originalPhoto)
                        binding.imagePreview.setImageBitmap(red)
                    }

                    1 -> {
                        val originalPhoto = (binding.imagePreview.drawable as BitmapDrawable).bitmap;
                        val green =
                            filers.greenImage(originalPhoto)
                        binding.imagePreview.setImageBitmap(green)
                    }

                    2 -> {
                        val originalPhoto = (binding.imagePreview.drawable as BitmapDrawable).bitmap;
                        val blue =
                            filers.blueImage(originalPhoto)
                        binding.imagePreview.setImageBitmap(blue)
                    }

                    3 -> {
                        val originalPhoto = (binding.imagePreview.drawable as BitmapDrawable).bitmap;
                        val mosaic =
                            filers.mosaicImage(originalPhoto)
                        binding.imagePreview.setImageBitmap(mosaic)
                    }

                    4 -> {
                        val originalPhoto = (binding.imagePreview.drawable as BitmapDrawable).bitmap;
                        val mirror =
                            filers.mirrorImage(originalPhoto)
                        binding.imagePreview.setImageBitmap(mirror)
                    }

                    5 -> {
                        val originalPhoto = (binding.imagePreview.drawable as BitmapDrawable).bitmap;
                        val negative =
                            filers.negativeImage(originalPhoto)
                        binding.imagePreview.setImageBitmap(negative)
                    }
                }
            }
        }

    }


    public fun seekBarRotate(seekBar: SeekBar, text: TextView) {
        seekBar.min = -180;
        seekBar.max = 180;
        seekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar, progress: Int, fromUser: Boolean) {
                text.text = progress.toString()
            }

            override fun onStartTrackingTouch(seekBar: SeekBar) {}

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
    public fun seekBarRetouch(
        firstSeekBar: SeekBar,
        firstText: TextView,
        secondSeekBar: SeekBar,
        secondText: TextView
    ) {
        firstSeekBar.min = 0
        firstSeekBar.max = 50
        secondSeekBar.min = 0
        secondSeekBar.max = 50

        firstSeekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(
                firstSeekBar: SeekBar,
                progress: Int,
                fromUser: Boolean
            ) {
                firstText.text = "@string/radius: ${progress}"
            }

            override fun onStartTrackingTouch(firstSeekBar: SeekBar) {}

            override fun onStopTrackingTouch(firstSeekBar: SeekBar) {}
        })

        secondSeekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(
                secondSeekBar: SeekBar,
                progress: Int,
                fromUser: Boolean
            ) {
                secondText.text = "\"@string/ratio\": ${progress}"
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

                        // Get the touch coordinates
                        val touchX = event.x
                        val touchY = event.y

                        // Calculate the actual image scale and position within the ImageView
                        val imageMatrixValues = FloatArray(9)
                        imageView.imageMatrix.getValues(imageMatrixValues)
                        val scaleX = imageMatrixValues[Matrix.MSCALE_X]
                        val scaleY = imageMatrixValues[Matrix.MSCALE_Y]
                        val translateX = imageMatrixValues[Matrix.MTRANS_X]
                        val translateY = imageMatrixValues[Matrix.MTRANS_Y]

                        // Remove the translation component to get the actual bitmap coordinates
                        val actualX = ((touchX - translateX) / scaleX).toInt()
                        val actualY = ((touchY - translateY) / scaleY).toInt()

                        // Ensure the coordinates are within the bitmap bounds
                        val clampedX = actualX.coerceIn(0, mutableBitmap.width - 1)
                        val clampedY = actualY.coerceIn(0, mutableBitmap.height - 1)

                        lifecycleScope.launch {
                            val radius = firstSeekBar.progress.toDouble()
                            val ratioRetouch = secondSeekBar.progress

                            // Perform the retouch
                            val retouchedBitmap = withContext(Dispatchers.Default) {
                                retuch.retush(
                                    mutableBitmap,
                                    clampedX.toDouble(),
                                    clampedY.toDouble(),
                                    radius,
                                    ratioRetouch
                                )
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


    @RequiresApi(Build.VERSION_CODES.O)
    public fun seekBarUnsharpMasking(
        firstSeekBar: SeekBar,
        firstText: TextView,
        secondSeekBar: SeekBar,
        secondText: TextView,
        button: Button
    ) {
        firstSeekBar.min = 2;
        firstSeekBar.max = 15;
        secondSeekBar.min = 1;
        secondSeekBar.max = 15;

        firstSeekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(
                firstSeekBar: SeekBar,
                progress: Int,
                fromUser: Boolean
            ) {
                firstText.text = "@string/radius: ${progress.toString()}"
            }

            override fun onStartTrackingTouch(firstSeekBar: SeekBar) {}

            override fun onStopTrackingTouch(firstSeekBar: SeekBar) {}
        })

        secondSeekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(
                secondSeekBar: SeekBar,
                progress: Int,
                fromUser: Boolean
            ) {
                secondText.text = "\"@string/ratio\": ${progress.toString()}"
            }

            override fun onStartTrackingTouch(secondSeekBar: SeekBar) {}

            override fun onStopTrackingTouch(secondSeekBar: SeekBar) {}
        })

        val originalPhoto = (binding.imagePreview.drawable as BitmapDrawable).bitmap;
        button.setOnClickListener {
            lifecycleScope.launch {
                val originalPhoto = (binding.imagePreview.drawable as BitmapDrawable).bitmap;
                val answerBitmap = mask.unsharpMasking(
                    originalPhoto,
                    firstSeekBar.progress, secondSeekBar.progress.toFloat()
                )
                binding.imagePreview.setImageBitmap(answerBitmap);
            }
        }
    }

    public fun seekBarScale(firstSeekBar: SeekBar, firstText: TextView) {
        firstSeekBar.min = 0
        firstSeekBar.max = 20;

        firstSeekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(
                firstSeekBar: SeekBar,
                progress: Int,
                fromUser: Boolean
            ) {
                firstText.text = "\"@string/ratio\": ${progress.toString()}"
            }

            override fun onStartTrackingTouch(firstSeekBar: SeekBar) {}

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

    public fun seekOffFirstBar() {
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

    public fun seekOffSecondBar() {
        val seek: SeekBar = findViewById(R.id.seekBar2)
        val textSetting: TextView = findViewById(R.id.secondText)
        seek.visibility = View.INVISIBLE
        seek.progress = seek.min
        textSetting.text = ""
        textSetting.visibility = View.INVISIBLE
    }

    public fun recyclerOn() {
        val recyc: RecyclerView = findViewById(R.id.filterRecycler)
        recyc.visibility = View.VISIBLE
    }

    public fun recycerOff() {
        val recyc: RecyclerView = findViewById(R.id.filterRecycler)
        recyc.visibility = View.INVISIBLE
    }


    public fun btnOnUnsharp() {
        val btn: Button = findViewById(R.id.firstButton)
        btn.visibility = View.VISIBLE
        btn.text = "@string/start"
    }

    public fun btnOfUnsharp() {
        val btn: Button = findViewById(R.id.firstButton)
        btn.visibility = View.INVISIBLE
        btn.text = ""
    }

    public fun seekOnCrypt() {
        val TextForCrypt: EditText = findViewById(R.id.editText)
        val leftButton: Button = findViewById(R.id.firstButton)
        val rightButton: Button = findViewById(R.id.secondButton)

        TextForCrypt.visibility = View.VISIBLE
        leftButton.visibility = View.VISIBLE
        rightButton.visibility = View.VISIBLE
        leftButton.setText("@string/code")
        rightButton.setText("@string/decryption")
        leftButton.setOnClickListener()
        {
            var cryptedPhoto = CryptText.CryptTextInImg(
                (binding.imagePreview.drawable as BitmapDrawable).bitmap,
                TextForCrypt.text.toString()
            )
            binding.imagePreview.setImageBitmap(cryptedPhoto)

        }
        rightButton.setOnClickListener()
        {
            var decryptedText =
                DecryptText.DeCryptTextInImg((binding.imagePreview.drawable as BitmapDrawable).bitmap)
            var toast = Toast.makeText(this, decryptedText, Toast.LENGTH_LONG)
            toast.show()

        }

    }
    public fun seekOffCrypt()
    {
        val TextForCrypt: EditText = findViewById(R.id.editText)
        val leftButton: Button = findViewById(R.id.firstButton)
        val rightButton: Button = findViewById(R.id.secondButton)

        TextForCrypt.visibility = View.INVISIBLE
        leftButton.visibility = View.INVISIBLE
        rightButton.visibility = View.INVISIBLE
    }

}