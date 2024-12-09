package com.rimetech.rimecounter.viewmodels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.switchMap
import com.rimetech.rimecounter.data.Counter
import com.rimetech.rimecounter.repositories.CounterRepository
import java.util.UUID

class ListCounterViewModel(application: Application) : AndroidViewModel(application) {
    private val counterRepository = CounterRepository.get()

    fun getCounter(id: UUID): LiveData<Counter> {
        return counterRepository.getCounter(id)
    }

    fun getCounters(): LiveData<MutableList<Counter>> {
        return counterRepository.getCounters()
    }

    private val _searchResults = MutableLiveData<String>()

    val searchResults: LiveData<MutableList<Counter>> = _searchResults.switchMap { keyWord ->
        counterRepository.searchCounters(keyWord)
    }

    fun addCounter(counter: Counter) = counterRepository.addCounter(counter)
    private fun deleteCounter(counter: Counter) = counterRepository.deleteCounter(counter)
    fun updateCounter(counter: Counter) = counterRepository.updateCounter(counter)
    fun updateCounters(counters: MutableList<Counter>) = counterRepository.updateCounters(counters)
    fun removeAllCounters() = counterRepository.removeAllCounters()
    fun setSearchKeyWord(keyWord: String) {
        _searchResults.value = keyWord
    }
}