package com.rimetech.rimecounter.viewmodels

import android.content.Context
import android.media.MediaPlayer
import android.net.Uri
import android.view.HapticFeedbackConstants
import android.view.View
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rimetech.rimecounter.R
import com.rimetech.rimecounter.data.Counter
import com.rimetech.rimecounter.repositories.CounterRepository
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.util.UUID

class CounterViewModel(id:UUID) : ViewModel() {
    private val counterRepository = CounterRepository.get()
    val counterLiveData = counterRepository.getCounter(id)
    lateinit var counter: Counter
    fun updateCounter(counter: Counter) = counterRepository.updateCounter(counter)

    private val _counterAuto = MutableLiveData(false)
    val counterAuto: LiveData<Boolean> get() = _counterAuto

    private val _counterMedia = MutableLiveData(false)
    val counterMedia: LiveData<Boolean> get() = _counterMedia

    private val _currentTime = MutableLiveData(0)
    val currentTime: LiveData<Int> get() = _currentTime

    private val _leftSeconds = MutableLiveData(0L)
    val leftSeconds: LiveData<Long> get() = _leftSeconds

    private val _currentValue = MutableLiveData(0)
    val currentValue: LiveData<Int> get() = _currentValue

    private fun toggleAuto(counter: Counter) {
        _counterAuto.value = !_counterAuto.value!!
        if (_counterAuto.value == true) {
            startAuto(counter)
        }
    }

    fun toggleMedia(counter: Counter,context: Context){
        _counterMedia.value = !_counterMedia.value!!
        if (_counterMedia.value==true){
            counter.autoMediaUri?.let {uri->
                startMedia(counter, uri,context)
            }
        }
    }

    private fun startAuto(counter: Counter) {
        viewModelScope.launch {
            while (_counterAuto.value == true) {
                var temp = counter.autoInSecond
                while (_counterAuto.value == true && temp > 0) {
                    _currentTime.value = temp
                    temp--
                    delay(1000L)
                }
                if (_counterAuto.value == true) {
                    counter.currentValue += counter.increaseValue
                    updateCounter(counter)
                }
            }

        }
    }

    fun startTarget(counter: Counter,onFinished: () -> Unit){
        viewModelScope.launch {
            counter.targetSeconds?.let {
                seconds->
                var temp = seconds
                while (temp>=0){
                    _leftSeconds.value=temp
                    temp--
                    delay(1000L)
                }
                onFinished()
            }
        }
    }

    private fun startMedia(counter: Counter, audioUri: Uri, context: Context) {
        viewModelScope.launch {
            val mediaPlayer = MediaPlayer()
            try {
                mediaPlayer.setDataSource(context, audioUri)
                mediaPlayer.prepare()

                while (_counterMedia.value == true) {
                    if (_counterMedia.value == true) {
                        mediaPlayer.start()
                        while (mediaPlayer.isPlaying && _counterMedia.value == true) {
                            delay(100L)
                        }
                        if (_counterMedia.value == true) {
                            counter.currentValue += counter.increaseValue
                            updateCounter(counter)
                        }
                        mediaPlayer.seekTo(0)
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            } finally {
                mediaPlayer.release()
            }
        }
    }

    private val _isTargetStarted = MutableLiveData<Boolean>()
    val isTargetStarted : LiveData<Boolean> get() = _isTargetStarted
    fun setIsTargetStarted(start:Boolean){
        _isTargetStarted.value=start
    }

    private val _targetSeconds = MutableLiveData<Long>()
    val targetSeconds:LiveData<Long> get() = _targetSeconds
    fun setTargetSeconds(seconds:Long){
        _targetSeconds.value=seconds
    }




    fun onCounterActivityViewClicked(view: View, counter: Counter){
        when(view.id){
            R.id.add_shape-> {
                view.performHapticFeedback(HapticFeedbackConstants.LONG_PRESS)
                counter.currentValue += counter.increaseValue
                updateCounter(counter)
            }
            R.id.action_minus->{
                view.performHapticFeedback(HapticFeedbackConstants.LONG_PRESS)
                counter.currentValue -= counter.decreaseValue
                updateCounter(counter)
            }
            R.id.action_time->{
                view.performHapticFeedback(HapticFeedbackConstants.LONG_PRESS)
                toggleAuto(counter)
            }
        }
    }



}