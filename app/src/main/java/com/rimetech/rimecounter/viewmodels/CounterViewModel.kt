package com.rimetech.rimecounter.viewmodels

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
    private fun updateCounter(counter: Counter) = counterRepository.updateCounter(counter)

    private val _counterAuto = MutableLiveData(false)
    val counterAuto: LiveData<Boolean> get() = _counterAuto

    private val _currentTime = MutableLiveData(0)
    val currentTime: LiveData<Int> get() = _currentTime

    private val _currentValue = MutableLiveData(0)
    val currentValue: LiveData<Int> get() = _currentValue

    private fun toggleAuto(counter: Counter) {
        _counterAuto.value = !_counterAuto.value!!
        if (_counterAuto.value == true) {
            startAuto(counter)
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