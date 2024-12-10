package com.rimetech.rimecounter.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import java.util.UUID

@Suppress("UNCHECKED_CAST")
class CounterViewModelFactory(private val id: UUID): ViewModelProvider.Factory  {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(CounterViewModel::class.java)){
            return CounterViewModel(id) as T
        } else {
            throw IllegalArgumentException("Unknown ViewModel Class!!!!!")
        }
    }
}