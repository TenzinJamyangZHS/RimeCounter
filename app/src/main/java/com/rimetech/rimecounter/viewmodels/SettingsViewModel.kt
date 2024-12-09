package com.rimetech.rimecounter.viewmodels

import android.app.Application
import android.content.Context
import androidx.appcompat.app.AppCompatDelegate
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.rimetech.rimecounter.R
import com.rimetech.rimecounter.utils.BG_AMOLED
import com.rimetech.rimecounter.utils.BG_FROM_COUNTER
import com.rimetech.rimecounter.utils.BG_NORMAL
import com.rimetech.rimecounter.utils.COLOR_BLUE
import com.rimetech.rimecounter.utils.COLOR_BROWN
import com.rimetech.rimecounter.utils.COLOR_DEFAULT
import com.rimetech.rimecounter.utils.COLOR_GREEN
import com.rimetech.rimecounter.utils.COLOR_INDIGO
import com.rimetech.rimecounter.utils.COLOR_ORANGE
import com.rimetech.rimecounter.utils.COLOR_PINK
import com.rimetech.rimecounter.utils.COLOR_PURPLE
import com.rimetech.rimecounter.utils.COLOR_RED
import com.rimetech.rimecounter.utils.COLOR_TEAL
import com.rimetech.rimecounter.utils.COLOR_YELLOW
import com.rimetech.rimecounter.utils.ICON_OUTLINE
import com.rimetech.rimecounter.utils.ICON_OUTLINE_ROUND
import com.rimetech.rimecounter.utils.ICON_ROUND
import com.rimetech.rimecounter.utils.ICON_SHARP
import com.rimetech.rimecounter.utils.SHAPE_NULL
import com.rimetech.rimecounter.utils.SHAPE_RECTANGLE
import com.rimetech.rimecounter.utils.SHAPE_ROUND
import com.rimetech.rimecounter.utils.THEME_DARK
import com.rimetech.rimecounter.utils.THEME_LIGHT
import com.rimetech.rimecounter.utils.THEME_SYSTEM
import com.rimetech.rimecounter.utils.tabNameList

class SettingsViewModel(application: Application) : AndroidViewModel(application) {
    private val sharedPreferences = getApplication<Application>().getSharedPreferences("app-settings", Context.MODE_PRIVATE)
    private val editor = sharedPreferences.edit()
    fun saveSettings(){
        editor.apply {
            putString("counter-shape",when(counterShape.value){
              else->null
            })
            putString("counter-background",when(counterBackground.value){
                else->null
            })
            putBoolean("is-monet",isMonet.value!!)
            putBoolean("is-blur",isBlur.value!!)
            putString("theme-id",when(themeId.value){
                else->null
            })
            putString("tab-color",when(tabColor.value){
                else->null
            })
            putString("tab-style",when(tabStyle.value){
                else->null
            })
        }.apply()
    }

    private val _counterShape = MutableLiveData<Int>()
    val counterShape:MutableLiveData<Int> get() = _counterShape
    fun setCounterShape(id:Int):Int{
        _counterShape.value=id
        return id
    }
    private fun initCounterShape(){
        val value = sharedPreferences.getString("counter-shape", SHAPE_ROUND)
        _counterShape.value = when(value){
            SHAPE_ROUND->R.id.round_shape
            SHAPE_NULL->R.id.null_shape
            SHAPE_RECTANGLE->R.id.round_rectangle_shape
            else->throw IllegalArgumentException("Unknown saved counter shape!")
        }
    }

    private val _counterBackground = MutableLiveData<Int>()
    val counterBackground:MutableLiveData<Int> get() = _counterBackground
    fun setCounterBackground(id:Int):Int{
        _counterBackground.value=id
        return id
    }
    private fun initCounterBackground(){
        val value = sharedPreferences.getString("counter-background", BG_FROM_COUNTER)
        _counterBackground.value = when(value){
            BG_FROM_COUNTER->R.id.from_counter_bg
            BG_NORMAL->R.id.normal_bg
            BG_AMOLED->R.id.amoled_bg
            else->throw IllegalArgumentException("Unknown saved counter background!")
        }
    }

    private val _isMonet = MutableLiveData<Boolean>()
    val isMonet : MutableLiveData<Boolean> get() = _isMonet
    fun setIsMonet(check:Boolean):Boolean{
        _isMonet.value=check
        return check
    }
    private fun initIsMonet(){
        val value = sharedPreferences.getBoolean("is-monet",false)
        _isMonet.value=value
    }

    private val _isBlur = MutableLiveData<Boolean>()
    val isBlur : MutableLiveData<Boolean> get() = _isBlur
    fun setIsBlur(check:Boolean):Boolean{
        _isBlur.value=check
        return check
    }
    private fun initIsBlur(){
        val value = sharedPreferences.getBoolean("is-blur",true)
        _isBlur.value=value
    }

    private val _themeId = MutableLiveData<Int>()
    val themeId :MutableLiveData<Int> get() = _themeId
    fun setThemeId(id: Int):Int{
        _themeId.value=id
        return id
    }
    private fun initThemeId(){
        val value = sharedPreferences.getString("theme-id", THEME_SYSTEM)
        _themeId.value = when(value){
            THEME_SYSTEM->R.id.theme_system
            THEME_LIGHT->R.id.theme_light
            THEME_DARK->R.id.theme_dark
            else->throw IllegalArgumentException("Unknown saved theme id!")
        }
    }

    private val _tabColor = MutableLiveData<Int>()
    val tabColor :MutableLiveData<Int> get() = _tabColor
    fun setTabColor(id: Int):Int{
        _tabColor.value=id
        return id
    }
    private fun initTabColor(){
        val value = sharedPreferences.getString("tab-color", COLOR_DEFAULT)
        _tabColor.value = when(value){
            COLOR_DEFAULT->R.id.color_default
            COLOR_RED->R.id.color_red
            COLOR_PINK->R.id.color_pink
            COLOR_PURPLE->R.id.color_purple
            COLOR_INDIGO->R.id.color_indigo
            COLOR_BLUE->R.id.color_blue
            COLOR_GREEN->R.id.color_green
            COLOR_YELLOW->R.id.color_yellow
            COLOR_ORANGE->R.id.color_orange
            COLOR_BROWN->R.id.color_brown
            COLOR_TEAL->R.id.color_teal
            else->throw IllegalArgumentException("Unknown saved tab color!")
        }
    }

    private val _tabStyle = MutableLiveData<Int>()
    val tabStyle :MutableLiveData<Int> get() = _tabStyle
    fun setTabStyle(id: Int):Int{
        _tabStyle.value=id
        return id
    }
    private fun initTabStyle(){
        val value = sharedPreferences.getString("tab-style", ICON_ROUND)
        _tabStyle.value = when(value){
            ICON_ROUND->R.id.icon_round
            ICON_SHARP->R.id.icon_sharp
            ICON_OUTLINE->R.id.icon_outline
            ICON_OUTLINE_ROUND->R.id.icon_outline_round
            else->throw IllegalArgumentException("Unknown saved tab style!")
        }
    }

    private val _tabRange = MutableLiveData<MutableList<String>>()
    val tabRange : LiveData<MutableList<String>> get() = _tabRange
    private fun initTabRange(){
        val valueSet = sharedPreferences.getStringSet("tab-range", null)
        _tabRange.value = valueSet?.toMutableList()?: tabNameList

    }

    private val _tabSize = MutableLiveData(7)
    val tabSize: LiveData<Int> get() = _tabSize
    fun setTabSize() {
        val metrics = getApplication<Application>().resources.displayMetrics
        val size = (metrics.widthPixels / metrics.density).toInt() / 70
        _tabSize.value = if (size > 7) 7 else size
    }

    private val _screenWidth = MutableLiveData<Int>()
    val screenWidth:LiveData<Int> get() = _screenWidth
    fun setScreenWidth(){_screenWidth.value=getApplication<Application>().resources.displayMetrics.widthPixels}

    private val _listWidth = MutableLiveData<Int>()
    val listWidth:LiveData<Int> get() = _listWidth
    fun setListWidth(){
        val metrics = getApplication<Application>().resources.displayMetrics
        val size = (metrics.widthPixels/metrics.density).toInt()/360
        _listWidth.value = if (size<1) 1 else size
    }

    private val _isListEnd = MutableLiveData(false)
    val isListEnd : LiveData<Boolean> get() = _isListEnd
    fun setIsListEnd(isEnd:Boolean){
        _isListEnd.value = isEnd
    }

    private val _tabPosition = MutableLiveData(0)
    val tabPosition: LiveData<Int> get() = _tabPosition
    fun setTabPosition(position: Int) {
        _tabPosition.value =
            if (position in 0 until 7) position else throw IllegalArgumentException("Wrong Position!!!")
    }

    val colorsMap =
        mapOf(
            R.id.color_default to R.color.color_default,
            R.id.color_red to R.color.color_red,
            R.id.color_pink to R.color.color_pink,
            R.id.color_purple to R.color.color_purple,
            R.id.color_indigo to R.color.color_indigo,
            R.id.color_blue to R.color.color_blue,
            R.id.color_green to R.color.color_green,
            R.id.color_yellow to R.color.color_yellow,
            R.id.color_orange to R.color.color_orange,
            R.id.color_brown to R.color.color_brown,
            R.id.color_teal to R.color.color_teal
        )
    val themeList = mutableListOf(
        R.id.theme_system to AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM,
        R.id.theme_dark to AppCompatDelegate.MODE_NIGHT_YES,
        R.id.theme_light to AppCompatDelegate.MODE_NIGHT_NO
    )



    init {
        initTabRange()
        initTabStyle()
        initTabColor()
        initThemeId()
        initCounterBackground()
        initCounterShape()
        initIsMonet()
        initIsBlur()
        setTabSize()
        setScreenWidth()
        setListWidth()
    }
}