package com.rimetech.rimecounter.viewmodels

import android.app.Application
import android.net.Uri
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.rimetech.rimecounter.R

class EditorViewModel(application: Application) : AndroidViewModel(application) {
    val colorsMap =
        mapOf(
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

    private val _counterColorId = MutableLiveData<Int>()
    val counterColorId:MutableLiveData<Int> get() = _counterColorId
    fun setCounterColorId(id:Int):Int{
        _counterColorId.value=id
        return id
    }

    private val _label = MutableLiveData<String>()
    val label:LiveData<String> get() = _label
    fun setLabel(text:String){
        _label.value=text
    }

    private val _actionText = MutableLiveData<String>()
    val actionText: LiveData<String> get() = _actionText
    fun setActionText(text: String){
        _actionText.value=text
    }

    private val _name = MutableLiveData<String>()
    val name:MutableLiveData<String> get() = _name
    fun setName(text: String):String{
        _name.value=text
        return text
    }

    private val _theValue = MutableLiveData<String>()
    val theValue:MutableLiveData<String> get() = _theValue
    fun setTheValue(text: String):String{
        _theValue.value=text
        return text
    }

    private val _resetValue = MutableLiveData<String>()
    val resetValue:MutableLiveData<String> get() = _resetValue
    fun setResetValue(text: String):String{
        _resetValue.value=text
        return text
    }

    private val _increaseValue = MutableLiveData<String>()
    val increaseValue:MutableLiveData<String> get() = _increaseValue
    fun setIncreaseValue(text: String):String{
        _increaseValue.value=text
        return text
    }

    private val _decreaseValue = MutableLiveData<String>()
    val decreaseValue:MutableLiveData<String> get() = _decreaseValue
    fun setDecreaseValue(text: String):String{
        _decreaseValue.value=text
        return text
    }

    private val _autoLength = MutableLiveData<String>()
    val autoLength:MutableLiveData<String> get() = _autoLength
    fun setAutoLength(text: String):String{
        _autoLength.value=text
        return text
    }

    private val _mediaUri = MutableLiveData<Uri>()
    val mediaUri :LiveData<Uri> get() = _mediaUri
    fun setMediaUri(uri: Uri){
        _mediaUri.value=uri
    }

    private val _targetValue = MutableLiveData<String>()
    val targetValue:MutableLiveData<String> get() = _targetValue
    fun setTargetValue(text: String):String{
        _targetValue.value=text
        return text
    }

    private val _targetCircles = MutableLiveData<String>()
    val targetCircles:MutableLiveData<String> get() = _targetCircles
    fun setTargetCircles(text: String):String{
        _targetCircles.value=text
        return text
    }

    private val _perCircleSeconds = MutableLiveData<String>()
    val perCircleSeconds:MutableLiveData<String> get() = _perCircleSeconds
    fun setPerCircleSeconds(text: String):String{
        _perCircleSeconds.value=text
        return text
    }
}