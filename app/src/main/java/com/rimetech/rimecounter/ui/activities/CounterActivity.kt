package com.rimetech.rimecounter.ui.activities

import android.annotation.SuppressLint
import android.app.UiModeManager
import android.graphics.PorterDuff
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.HapticFeedbackConstants
import android.view.View
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import com.rimetech.rimecounter.R
import com.rimetech.rimecounter.app.RimeCounter
import com.rimetech.rimecounter.data.Counter
import com.rimetech.rimecounter.databinding.ActivityCounterBinding
import com.rimetech.rimecounter.utils.POS_BOTTOM
import com.rimetech.rimecounter.utils.POS_TOP
import com.rimetech.rimecounter.utils.adjustColorTowardsBlackOrWhite
import com.rimetech.rimecounter.utils.colorToRColorList
import com.rimetech.rimecounter.utils.dpToPx
import com.rimetech.rimecounter.utils.formatDuration
import com.rimetech.rimecounter.utils.getFormattedDate
import com.rimetech.rimecounter.utils.getNavigationBarHeight
import com.rimetech.rimecounter.utils.getStatusBarHeight
import com.rimetech.rimecounter.utils.getTimeDifferenceInSeconds
import com.rimetech.rimecounter.utils.setBlur
import com.rimetech.rimecounter.utils.setDragOnXYAxisLimit
import com.rimetech.rimecounter.utils.setMargin
import com.rimetech.rimecounter.utils.setPaintBackground
import com.rimetech.rimecounter.viewmodels.CounterViewModel
import com.rimetech.rimecounter.viewmodels.CounterViewModelFactory
import com.rimetech.rimecounter.viewmodels.SettingsViewModel
import java.util.Date
import java.util.UUID

@Suppress("DEPRECATION", "INFERRED_TYPE_VARIABLE_INTO_POSSIBLE_EMPTY_INTERSECTION")
class CounterActivity : AppCompatActivity() {
    private lateinit var counterId: UUID
    private val counterBinding by lazy { ActivityCounterBinding.inflate(layoutInflater) }
    private val counterViewModel by lazy {
        ViewModelProvider(
            this,
            CounterViewModelFactory(counterId)
        )[CounterViewModel::class.java]
    }
    private lateinit var settingsViewModel: SettingsViewModel
    private var runnable: Runnable? = null
    private val handler = Handler(Looper.getMainLooper())
    private lateinit var onBackPressedCallback: OnBackPressedCallback
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        supportActionBar?.hide()
        setContentView(counterBinding.root)
        getCounterId()
        RimeCounter.counterActivityList.add(counterId to this)
        settingsViewModel = (application as RimeCounter).settingsViewModel
        settingsViewModel.addCounterTimeTask(counterId, false)
        settingsViewModel.addCounterMediaTask(counterId, false)
        counterBinding.settingsViewModel = settingsViewModel
        counterBinding.counterViewModel = counterViewModel
        counterBinding.lifecycleOwner = this
        counterViewModel.counterLiveData.observe(this) { counter ->
            counterBinding.counter = counter
            setUI(counter)
            updateTimeDifference(counter.startTime)
            updateRunningTime()
            setMediaCount(counter)
            setAutoCount(counter)
            setMediaAndTimeButton(counter)
        }
        setMargin()
        setBlurView()

        onBackPressedCallback = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                moveTaskToBack(true)
            }
        }
        onBackPressedDispatcher.addCallback(this, onBackPressedCallback)

    }

    override fun onDestroy() {
        super.onDestroy()
        RimeCounter.counterActivityList.remove(counterId to this)
        settingsViewModel.updateTimeTaskStats(counterId, false)
        settingsViewModel.updateMediaTaskStats(counterId, false)
        settingsViewModel.removeCounterTimeTask(counterId)
        settingsViewModel.removeCounterMediaTask(counterId)
        runnable?.let {
            handler.removeCallbacks(it)
            runnable = null
        }
    }

    private fun setMargin() {
        counterBinding.timeLayout.setMargin(getStatusBarHeight(this), POS_TOP)
        counterBinding.blurView.setMargin(getNavigationBarHeight(this), POS_BOTTOM)
    }

    private fun setBlurView() {
        settingsViewModel.isBlur.observe(this) { blur ->
            counterBinding.blurView.apply {
                setPaintBackground(
                    if (blur) 155 else 255,
                    this@CounterActivity.dpToPx(8f),
                    ContextCompat.getColor(this@CounterActivity, R.color.counter_action_bg)
                )
                if (blur) setBlur(this@CounterActivity, 18f) else setBlurEnabled(false)
            }
        }
        counterBinding.dragIcon.isClickable =false
        counterBinding.blurView.setDragOnXYAxisLimit()
        counterBinding.addShape.setDragOnXYAxisLimit()
    }

    private fun getCounterId() {
        counterId = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            intent.getParcelableExtra("counter-id", UUID::class.java)
                ?: throw IllegalArgumentException("Error when get the counter id")
        } else {
            intent.getParcelableExtra("counter-id")
                ?: throw IllegalArgumentException("Error when get the counter id")
        }
    }

    private fun setUI(counter: Counter) {
        counterBinding.name.isSelected = true
        counterBinding.startTime.isSelected = true
        counterBinding.runningTime.isSelected = true
        val createDate = getFormattedDate(counter.startTime, "yyyy-MM-dd-HH:mm:ss")
        counterBinding.startTime.text = createDate

        settingsViewModel.counterShape.observe(this) { shape ->
            settingsViewModel.counterBackground.observe(this) { background ->
                settingsViewModel.isMonet.observe(this) { monet ->
                    settingsViewModel.themeId.observe(this) { theme ->

                        setAddShape(shape, counter)

                        when (background) {
                            R.id.normal_bg -> {
                                counterBinding.counterRoot.setBackgroundColor(
                                    ContextCompat.getColor(
                                        this,
                                        if (monet) R.color.color_root_monet else R.color.color_root
                                    )
                                )
                            }

                            R.id.from_counter_bg -> {
                                setFromCounter(counter, theme)
                            }

                            R.id.amoled_bg -> {
                                setAmoled()
                            }
                        }

                    }

                }

            }
        }
    }

    private fun setAddShape(shape: Int, counter: Counter) {
        when (shape) {
            R.id.null_shape -> {
                counterBinding.addShape.setBackgroundColor(
                    ContextCompat.getColor(this, R.color.color_transparent)
                )
                counterBinding.value.setTextColor(
                    ContextCompat.getColor(
                        this,
                        R.color.no_shape_font_color
                    )
                )
            }

            R.id.round_shape -> {
                counterBinding.addShape.setPaintBackground(
                    255,
                    this.dpToPx(2000f),
                    ContextCompat.getColor(
                        this,
                        colorToRColorList.find { it.first == counter.color }?.second
                            ?: throw IllegalArgumentException("Counter color not found!")
                    )
                )
                counterBinding.value.setTextColor(
                    ContextCompat.getColor(
                        this,
                        R.color.shape_font_color
                    )
                )
            }

            R.id.round_rectangle_shape -> {
                counterBinding.addShape.setPaintBackground(
                    255,
                    this.dpToPx(24f),
                    ContextCompat.getColor(
                        this,
                        colorToRColorList.find { it.first == counter.color }?.second
                            ?: throw IllegalArgumentException("Counter color not found!")
                    )
                )
                counterBinding.value.setTextColor(
                    ContextCompat.getColor(
                        this,
                        R.color.shape_font_color
                    )
                )
            }
        }
    }

    private fun setFromCounter(counter: Counter, theme: Int) {
        val colorValue =
            colorToRColorList.find { it.first == counter.color }?.second
                ?: throw IllegalArgumentException("Counter color not found!")
        val counterColor = ContextCompat.getColor(this, colorValue)
        val radio = when (theme) {
            R.id.theme_system -> {
                if (getSystemService(
                        UiModeManager::class.java
                    ).nightMode == UiModeManager.MODE_NIGHT_YES
                ) -0.8f else 0.8f
            }

            R.id.theme_light -> 0.8f
            R.id.theme_dark -> -0.8f
            else -> throw IllegalArgumentException("Unknown Theme!")
        }
        val backgroundColor =
            adjustColorTowardsBlackOrWhite(counterColor, radio)
        counterBinding.counterRoot.setBackgroundColor(backgroundColor)
    }

    private fun setAmoled() {
        counterBinding.counterRoot.setBackgroundColor(
            ContextCompat.getColor(
                this,
                R.color.black
            )
        )
        counterBinding.addShape.setBackgroundColor(
            ContextCompat.getColor(
                this,
                R.color.black
            )
        )

        counterBinding.startTime.setTextColor(
            ContextCompat.getColor(
                this,
                R.color.amoled_font
            )
        )
        counterBinding.runningTime.setTextColor(
            ContextCompat.getColor(
                this,
                R.color.amoled_font
            )
        )
        counterBinding.value.setTextColor(
            ContextCompat.getColor(
                this,
                R.color.amoled_font
            )
        )
        counterBinding.autoText.setTextColor(
            ContextCompat.getColor(
                this,
                R.color.amoled_font
            )
        )
        counterBinding.actionMinus.setColorFilter(
            ContextCompat.getColor(
                this, R.color.amoled_font
            ), PorterDuff.Mode.SRC_IN
        )
        counterBinding.actionMedia.setColorFilter(
            ContextCompat.getColor(
                this, R.color.amoled_font
            ), PorterDuff.Mode.SRC_IN
        )
        counterBinding.actionTime.setColorFilter(
            ContextCompat.getColor(
                this, R.color.amoled_font
            ), PorterDuff.Mode.SRC_IN
        )
        counterBinding.autoIcon.setColorFilter(
            ContextCompat.getColor(
                this, R.color.amoled_font
            ), PorterDuff.Mode.SRC_IN
        )
    }

    private fun updateTimeDifference(date: Date) {
        runnable?.let { handler.removeCallbacks(it) }

        runnable = object : Runnable {
            override fun run() {
                val secondsDiff = getTimeDifferenceInSeconds(date)
                val formattedDuration = formatDuration(secondsDiff)

                counterBinding.runningTime.text = formattedDuration

                handler.postDelayed(this, 1000)
            }
        }
        handler.post(runnable!!)
    }

    private fun updateRunningTime() {
        runnable?.let {
            handler.post(it)
        }
    }

    @SuppressLint("SetTextI18n")
    private fun setAutoCount(counter: Counter) {
        counterViewModel.counterAuto.observe(this) { auto ->
            counterBinding.addShape.isEnabled = !auto
            counterBinding.actionMedia.isEnabled = !auto
            counterBinding.actionMinus.isEnabled = !auto
            onBackPressedCallback.isEnabled = auto
            settingsViewModel.updateTimeTaskStats(counterId, auto)
            if (auto){
                counterBinding.autoIcon.setImageResource(R.drawable.action_time_24)
                counterBinding.autoIcon.setColorFilter(ContextCompat.getColor(this,
                    colorToRColorList.find { it.first==counter.color }?.second?:throw IllegalArgumentException("Counter color not found")))
            } else {
                counterBinding.autoIcon.setImageResource(R.drawable.empty_bg)
                counterBinding.autoIcon.clearColorFilter()
            }
            counterBinding.autoText.visibility = if (auto) View.VISIBLE else View.GONE
        }
        counterViewModel.currentTime.observe(this) { time ->
            counterBinding.autoText.text = " left $time seconds "
        }
    }

    private fun setMediaCount(counter: Counter) {
        counterViewModel.counterMedia.observe(this) { media ->
            counterBinding.addShape.isEnabled = !media
            counterBinding.actionTime.isEnabled = !media
            counterBinding.actionMinus.isEnabled = !media
            onBackPressedCallback.isEnabled = media
            settingsViewModel.updateMediaTaskStats(counterId, media)
            if (media){
                counterBinding.mediaIcon.setImageResource(R.drawable.action_media_24)
                counterBinding.mediaIcon.setColorFilter(ContextCompat.getColor(this,
                    colorToRColorList.find { it.first==counter.color }?.second?:throw IllegalArgumentException("Counter color not found")))
            } else {
                counterBinding.mediaIcon.setImageResource(R.drawable.empty_bg)
                counterBinding.mediaIcon.clearColorFilter()
            }

        }
    }

    private fun setMediaAndTimeButton(counter: Counter) {
        counterBinding.actionMedia.apply {
            setOnLongClickListener { view ->
                view.performHapticFeedback(HapticFeedbackConstants.LONG_PRESS)
                if (counter.autoMediaUri != null) {
                    counter.autoMediaUri?.let {
                        counterViewModel.toggleMedia(counter, this@CounterActivity)
                    }
                } else {
                    Toast.makeText(this@CounterActivity, "No media file!", Toast.LENGTH_SHORT)
                        .show()
                }
                true
            }
        }
        counterBinding.actionTime.apply {
            setOnLongClickListener { view->
                view.performHapticFeedback(HapticFeedbackConstants.LONG_PRESS)
                counterViewModel.toggleAuto(counter)
                true
            }
        }
    }

}