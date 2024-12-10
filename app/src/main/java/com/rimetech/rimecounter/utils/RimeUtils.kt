@file:Suppress("DEPRECATION")

package com.rimetech.rimecounter.utils

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.animation.ValueAnimator
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.graphics.Color
import android.graphics.Paint
import android.graphics.PorterDuff
import android.graphics.drawable.ShapeDrawable
import android.graphics.drawable.shapes.RoundRectShape
import android.os.Build
import android.os.SystemClock
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.view.ViewOutlineProvider
import android.view.animation.AnticipateInterpolator
import android.view.animation.OvershootInterpolator
import android.widget.ImageView
import android.widget.RadioButton
import androidx.annotation.ColorInt
import androidx.core.content.ContextCompat
import androidx.databinding.BindingAdapter
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.tabs.TabLayout
import com.rimetech.rimecounter.R
import eightbitlab.com.blurview.BlurView
import eightbitlab.com.blurview.RenderScriptBlur
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import kotlin.math.abs

const val HOME = "Home"
const val SEARCH = "Search"
const val FAVOR = "Favor"
const val HISTORY = "History"
const val SETTINGS = "Settings"
const val ARCHIVE = "Archive"
const val LOCK = "Lock"

const val ICON_ROUND = "icon-round"
const val ICON_SHARP = "icon-sharp"
const val ICON_OUTLINE = "icon-outline"
const val ICON_OUTLINE_ROUND = "icon-outline-round"

const val COLOR_DEFAULT = "color-default"
const val COLOR_RED = "color-red"
const val COLOR_PINK = "color-pink"
const val COLOR_PURPLE = "color-purple"
const val COLOR_INDIGO = "color-indigo"
const val COLOR_BLUE = "color-blue"
const val COLOR_GREEN = "color-green"
const val COLOR_YELLOW = "color-yellow"
const val COLOR_ORANGE = "color-orange"
const val COLOR_BROWN = "color-brown"
const val COLOR_TEAL = "color-teal"

const val THEME_SYSTEM = "theme-system"
const val THEME_LIGHT = "theme-light"
const val THEME_DARK = "theme-dark"

const val BG_NORMAL = "bg-normal"
const val BG_FROM_COUNTER = "bg-from-counter"
const val BG_AMOLED = "bg-amoled"

const val SHAPE_ROUND = "shape-round"
const val SHAPE_RECTANGLE = "shape-rectangle"
const val SHAPE_NULL = "shape-null"

const val POS_LEFT = 0
const val POS_TOP = 1
const val POS_RIGHT = 2
const val POS_BOTTOM = 3
const val POS_VER = 4
const val POS_HOR = 5
const val POS_ALL = 6
const val TOWARD_LEFT = 0
const val TOWARD_RIGHT = 1
const val TOWARD_UP = 2
const val TOWARD_DOWN = 3
const val HOME_LIST = 0
const val LIKED_LIST = 1
const val ARCHIVED_LIST = 2
const val LOCKED_LIST = 3


val tabNameList = mutableListOf(HOME, SEARCH, FAVOR, HISTORY, SETTINGS, ARCHIVE, LOCK)

val tabIconRoundList = mutableListOf(
    HOME to R.drawable.round_home_48,
    SEARCH to R.drawable.round_search_48,
    FAVOR to R.drawable.round_favorite_48,
    HISTORY to R.drawable.round_history_48,
    SETTINGS to R.drawable.round_settings_48,
    ARCHIVE to R.drawable.round_archive_48,
    LOCK to R.drawable.round_lock_48
)

val tabIconSharpList = mutableListOf(
    HOME to R.drawable.sharp_home_48,
    SEARCH to R.drawable.sharp_search_48,
    FAVOR to R.drawable.sharp_favorite_48,
    HISTORY to R.drawable.sharp_history_48,
    SETTINGS to R.drawable.sharp_settings_48,
    ARCHIVE to R.drawable.sharp_archive_48,
    LOCK to R.drawable.sharp_lock_48
)

val tabIconOutLineList = mutableListOf(
    HOME to R.drawable.outline_home_48,
    SEARCH to R.drawable.sharp_search_48,
    FAVOR to R.drawable.outline_favorite_48,
    HISTORY to R.drawable.sharp_history_48,
    SETTINGS to R.drawable.outline_settings_48,
    ARCHIVE to R.drawable.outline_archive_48,
    LOCK to R.drawable.outline_lock_48
)

val tabIconOutlineRoundList = mutableListOf(
    HOME to R.drawable.outline_rounded_home_48,
    SEARCH to R.drawable.round_search_48,
    FAVOR to R.drawable.outline_round_favorite_48,
    HISTORY to R.drawable.outline_rounded_history_48,
    SETTINGS to R.drawable.outline_rounded_settings_48,
    ARCHIVE to R.drawable.outline_rounded_archive_48,
    LOCK to R.drawable.outline_rounded_lock_48
)

val colorToRColorList = mutableListOf(
    COLOR_RED to R.color.color_red,
    COLOR_PINK to R.color.color_pink,
    COLOR_PURPLE to R.color.color_purple,
    COLOR_INDIGO to R.color.color_indigo,
    COLOR_BLUE to R.color.color_blue,
    COLOR_GREEN to R.color.color_green,
    COLOR_YELLOW to R.color.color_yellow,
    COLOR_ORANGE to R.color.color_orange,
    COLOR_BROWN to R.color.color_brown,
    COLOR_TEAL to R.color.color_teal
)

val colorToRIdList = mutableListOf(
    COLOR_RED to R.id.color_red,
    COLOR_PINK to R.id.color_pink,
    COLOR_PURPLE to R.id.color_purple,
    COLOR_INDIGO to R.id.color_indigo,
    COLOR_BLUE to R.id.color_blue,
    COLOR_GREEN to R.id.color_green,
    COLOR_YELLOW to R.id.color_yellow,
    COLOR_ORANGE to R.id.color_orange,
    COLOR_BROWN to R.id.color_brown,
    COLOR_TEAL to R.id.color_teal
)

fun Context.dpToPx(dp: Float): Float {
    return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, this.resources.displayMetrics)
}

@SuppressLint("DiscouragedApi", "InternalInsetResource")
fun getNavigationBarHeight(context: Context): Int {
    val navigationBarHeightResId =
        context.resources.getIdentifier("navigation_bar_height", "dimen", "android")
    return if (navigationBarHeightResId > 0) {
        context.resources.getDimensionPixelSize(navigationBarHeightResId)
    } else {
        0
    }
}

@SuppressLint("DiscouragedApi", "InternalInsetResource")
fun getStatusBarHeight(context: Context): Int {
    val statusBarHeightResId =
        context.resources.getIdentifier("status_bar_height", "dimen", "android")
    return if (statusBarHeightResId > 0) {
        context.resources.getDimensionPixelSize(statusBarHeightResId)
    } else {
        0
    }
}

fun View.setMargin(margin: Int, pos: Int) {
    val layoutParams = this.layoutParams as ViewGroup.MarginLayoutParams
    when (pos) {
        POS_LEFT -> layoutParams.leftMargin = margin
        POS_TOP -> layoutParams.topMargin = margin
        POS_RIGHT -> layoutParams.rightMargin = margin
        POS_BOTTOM -> layoutParams.bottomMargin = margin
        POS_HOR -> {
            layoutParams.rightMargin = margin
            layoutParams.leftMargin = margin
        }

        POS_VER -> {
            layoutParams.topMargin = margin
            layoutParams.bottomMargin = margin
        }

        POS_ALL -> {
            layoutParams.topMargin = margin
            layoutParams.bottomMargin = margin
            layoutParams.rightMargin = margin
            layoutParams.leftMargin = margin
        }

        else -> throw Exception("Wrong Pos!!!")
    }
}

fun View.setPaintBackground(opacity: Int, cornerRadius: Float, color: Int) {
    this.background = ShapeDrawable().apply {
        shape = RoundRectShape(FloatArray(8) { cornerRadius }, null, null)
        paint.color = color
        paint.alpha = opacity
        paint.style = Paint.Style.FILL
    }
}

fun BlurView.setBlur(activity: Activity, blurRadius: Float) {
    val decorView = activity.window.decorView
    val rootView = decorView.findViewById<ViewGroup>(android.R.id.content)
    val windowBackground = decorView.background

    if (Build.VERSION.SDK_INT < Build.VERSION_CODES.S) {
        this.setupWith(rootView, RenderScriptBlur(activity)).setFrameClearDrawable(windowBackground)
            .setBlurRadius(blurRadius)
    } else {
        this.setupWith(rootView).setFrameClearDrawable(windowBackground).setBlurRadius(blurRadius)
    }

    this.outlineProvider = ViewOutlineProvider.BACKGROUND
    this.setClipToOutline(true)
}

fun View.clickWithAnimation() {
    this.apply {
        val animator = ValueAnimator.ofFloat(1.4f, 1.0f).apply {
            duration = 300
            interpolator = OvershootInterpolator()
            addUpdateListener { animation ->
                val animatedValue = animation.animatedValue as Float
                scaleY = animatedValue
            }
            addListener(object : AnimatorListenerAdapter() {
                override fun onAnimationEnd(animation: Animator) {
                    super.onAnimationEnd(animation)
                    scaleY = 1f
                }
            })
        }
        animator.start()
    }
}

@SuppressLint("ClickableViewAccessibility")
fun TabLayout.setOnTabDoubleClickListener(doubleClickListener: (TabLayout.Tab) -> Unit) {
    var lastClickTime = 0L
    for (i in 0 until tabCount) {
        val tab = getTabAt(i)
        tab?.view?.setOnTouchListener { _, event ->
            if (event.action == MotionEvent.ACTION_DOWN) {
                val clickTime = SystemClock.elapsedRealtime()
                if (clickTime - lastClickTime < 200) {
                    doubleClickListener(tab)
                }
                lastClickTime = clickTime
            }
            false
        }
    }
}

fun createCustomTabView(
    layoutId: Int, imageId: Int,
    iconId: Int, colorId: Int, context: Context, colorMap: Map<Int, Int>
): View {
    val tabView = LayoutInflater.from(context).inflate(layoutId, null)
    val tabImage = tabView.findViewById<ImageView>(imageId)
    tabImage.setImageResource(iconId)
    if (colorId != R.id.color_default) {
        tabImage.setColorFilter(
            ContextCompat.getColor(
                context, colorMap[colorId]!!
            ), PorterDuff.Mode.SRC_IN
        )
    }

    tabView.isClickable = false
    tabImage.isClickable = false
    return tabView
}

fun View.longClickWithAnimation() {
    this.apply {
        val rotateLeftRight = ObjectAnimator.ofFloat(this, "rotation", -15f, 15f, -15f, 0f)
        rotateLeftRight.duration = 100
        rotateLeftRight.repeatCount = 2

        rotateLeftRight.repeatMode = ObjectAnimator.REVERSE

        val animatorSet = AnimatorSet()
        animatorSet.play(rotateLeftRight)
        animatorSet.start()
    }
}

fun View.showOrHideOnEnd(needToHide: Boolean) {
    if (needToHide) {
        this.animate().translationY(this.height.toFloat()).setDuration(300).setInterpolator(
            AnticipateInterpolator()
        ).withEndAction { this.visibility = View.GONE }.start()
    } else {
        this.visibility = View.VISIBLE
        this.animate().translationY(0f).setDuration(300).setInterpolator(
            OvershootInterpolator(0f)
        ).start()
    }
}

fun RecyclerView.setItemDecoration(decoration: RecyclerView.ItemDecoration) {
    while (itemDecorationCount > 0) {
        removeItemDecorationAt(0)
    }
    addItemDecoration(decoration)
}

fun RecyclerView.removeSpecificItemDecoration(decorationClass: Class<out RecyclerView.ItemDecoration>) {
    for (i in 0 until itemDecorationCount) {
        val decoration = getItemDecorationAt(i)
        if (decoration.javaClass == decorationClass) {
            removeItemDecoration(decoration)
            break
        }
    }
}

fun View.setDragOnYAxis() {
    // 记录视图的初始位置
    var initialY = 0f
    var initialTouchY = 0f
    var isDragging = false

    this.setOnTouchListener { v, event ->
        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                initialTouchY = event.rawY
                initialY = v.y
                isDragging = false
                true
            }

            MotionEvent.ACTION_MOVE -> {
                val dy = event.rawY - initialTouchY
                if (!isDragging && abs(dy) > 10) {
                    isDragging = true
                }
                if (isDragging) {
                    v.y = initialY + dy
                }
                true
            }

            MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> {
                if (!isDragging) {
                    v.performClick()
                }
                true
            }

            else -> false
        }
    }
}


fun View.clickWithAnimation2() {
    this.apply {
        val scaleUp = ValueAnimator.ofFloat(1f, 1.1f).apply {
            duration = 150
            interpolator = OvershootInterpolator()
            addUpdateListener { animation ->
                val animatedValue = animation.animatedValue as Float
                scaleX = animatedValue
                scaleY = animatedValue
            }
        }

        val scaleDown = ValueAnimator.ofFloat(1.1f, 0.9f).apply {
            duration = 150
            interpolator = OvershootInterpolator()
            addUpdateListener { animation ->
                val animatedValue = animation.animatedValue as Float
                scaleX = animatedValue
                scaleY = animatedValue
            }
        }

        val scaleRestore = ValueAnimator.ofFloat(0.9f, 1f).apply {
            duration = 150
            interpolator = OvershootInterpolator()
            addUpdateListener { animation ->
                val animatedValue = animation.animatedValue as Float
                scaleX = animatedValue
                scaleY = animatedValue
            }
        }

        scaleUp.addListener(object : AnimatorListenerAdapter() {
            override fun onAnimationEnd(animation: Animator) {
                super.onAnimationEnd(animation)
                scaleDown.start()
            }
        })

        scaleDown.addListener(object : AnimatorListenerAdapter() {
            override fun onAnimationEnd(animation: Animator) {
                super.onAnimationEnd(animation)
                scaleRestore.start()
            }
        })

        scaleUp.start()
    }

}

@SuppressLint("ClickableViewAccessibility")
fun View.setOnDoubleClickListener(interval: Long = 300, onDoubleClick: (View) -> Unit) {
    var lastClickTime = 0L

    setOnTouchListener { _, event ->
        if (event.action == MotionEvent.ACTION_DOWN) {
            val currentTime = SystemClock.elapsedRealtime()
            if (currentTime - lastClickTime <= interval) {
                onDoubleClick(this)
            }
            lastClickTime = currentTime
        }
        false
    }
}

@ColorInt
fun adjustColorTowardsBlackOrWhite(@ColorInt color: Int, factor: Float): Int {
    require(factor in -1.0..1.0) { "factor 必须在 [-1.0, 1.0] 范围内" }

    val alpha = Color.alpha(color)
    val red = Color.red(color)
    val green = Color.green(color)
    val blue = Color.blue(color)

    val newRed = if (factor < 0) {
        (red * (1 + factor)).coerceIn(0f, 255f).toInt()
    } else {
        (red + (255 - red) * factor).coerceIn(0f, 255f).toInt()
    }

    val newGreen = if (factor < 0) {
        (green * (1 + factor)).coerceIn(0f, 255f).toInt()
    } else {
        (green + (255 - green) * factor).coerceIn(0f, 255f).toInt()
    }

    val newBlue = if (factor < 0) {
        (blue * (1 + factor)).coerceIn(0f, 255f).toInt()
    } else {
        (blue + (255 - blue) * factor).coerceIn(0f, 255f).toInt()
    }

    return Color.argb(alpha, newRed, newGreen, newBlue)
}

fun getTimeDifferenceInSeconds(date: Date): Long {
    val currentTime = Date()
    val timeDifference = currentTime.time - date.time
    return timeDifference / 1000
}

fun formatDuration(seconds: Long): String {
    val years = seconds / (365 * 24 * 60 * 60)
    val months = (seconds % (365 * 24 * 60 * 60)) / (30 * 24 * 60 * 60)
    val days = (seconds % (30 * 24 * 60 * 60)) / (24 * 60 * 60)
    val hours = (seconds % (24 * 60 * 60)) / (60 * 60)
    val minutes = (seconds % (60 * 60)) / 60
    val remainingSeconds = seconds % 60
    return buildString {
        if (years > 0) append("$years y ")
        if (months > 0) append("$months m ")
        if (days > 0) append("$days d ")
        if (hours > 0) append("$hours h ")
        if (minutes > 0) append("$minutes m ")
        append("$remainingSeconds s ")
    }.trim()
}

fun getFormattedDate(date: Date, format: String): String {
    val formatter = SimpleDateFormat(format, Locale.getDefault())
    return formatter.format(date)
}




@BindingAdapter("drawableColorById")
fun setRadioButtonDrawableColorById(radioButton: RadioButton, idToColorMap: Map<Int, Int>?) {
    idToColorMap?.let { map ->
        val colorResId = map[radioButton.id]
        colorResId?.let { resId ->
            val color = ContextCompat.getColor(radioButton.context, resId)
            val drawable = radioButton.compoundDrawablesRelative[0]
            if (drawable != null) {
                drawable.setTint(color)
                radioButton.setCompoundDrawablesRelativeWithIntrinsicBounds(
                    drawable,
                    null,
                    null,
                    null
                )
            }
        }
    }
}


