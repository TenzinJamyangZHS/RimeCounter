package com.rimetech.rimecounter.viewmodels

import android.app.Application
import android.view.HapticFeedbackConstants
import android.view.View
import android.widget.Toast
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.switchMap
import com.rimetech.rimecounter.R
import com.rimetech.rimecounter.data.Counter
import com.rimetech.rimecounter.repositories.CounterRepository
import com.rimetech.rimecounter.ui.adapters.CounterListAdapter
import java.util.Date
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

    fun onListButtonClicked(view: View, counter: Counter) {
        when (view.id) {
            R.id.item_add_button -> {
                view.performHapticFeedback(HapticFeedbackConstants.CONTEXT_CLICK)
                counter.currentValue += counter.increaseValue
                updateCounter(counter)
            }

            R.id.item_minus_button -> {
                view.performHapticFeedback(HapticFeedbackConstants.CONTEXT_CLICK)
                counter.currentValue -= counter.decreaseValue
                updateCounter(counter)
            }

            R.id.item_favor_button -> {
                view.performHapticFeedback(HapticFeedbackConstants.CONTEXT_CLICK)
                counter.isFavored=!counter.isFavored
                Toast.makeText(
                    getApplication(), if (counter.isFavored
                    ) "Favored!" else "Unfavored!", Toast.LENGTH_SHORT
                ).show()
                updateCounter(counter)

            }

            R.id.item_reset_button -> {
                view.performHapticFeedback(HapticFeedbackConstants.CONTEXT_CLICK)
                counter.usageList.add((counter.startTime to Date()) to counter.currentValue)
                counter.currentValue = counter.resetValue
                counter.startTime = Date()
                Toast.makeText(
                    getApplication(), if (counter.currentValue == counter.resetValue
                    ) "Reset!" else "Not Reset!", Toast.LENGTH_SHORT
                ).show()
                updateCounter(counter)

            }

            R.id.item_lock_button -> {
                view.performHapticFeedback(HapticFeedbackConstants.CONTEXT_CLICK)
                counter.isLocked = !counter.isLocked
                Toast.makeText(
                    getApplication(), if (counter.isLocked
                    ) "Locked!" else "Not Locked!", Toast.LENGTH_SHORT
                ).show()
                updateCounter(counter)
            }


            R.id.item_delete_button -> {
                view.performHapticFeedback(HapticFeedbackConstants.CONTEXT_CLICK)
                deleteCounter(counter)
                Toast.makeText(
                    getApplication(),
                    if (getCounter(counter.id).value == null) "Deleted!" else "Not Deleted!",
                    Toast.LENGTH_SHORT
                ).show()
            }
            R.id.list_item_root_alt->{
                view.performHapticFeedback(HapticFeedbackConstants.CONTEXT_CLICK)
                updateCounter(counter)
            }

        }
    }

    private val _counterListHome = MutableLiveData<MutableList<Counter>>()
    val counterListHome: LiveData<MutableList<Counter>> get() = _counterListHome
    fun setCounterList(list: MutableList<Counter>) {
        _counterListHome.value = list
    }

    private val _counterListArchived = MutableLiveData<MutableList<Counter>>()
    val counterListArchived: LiveData<MutableList<Counter>> get() = _counterListArchived
    fun setCounterListArchived(list: MutableList<Counter>) {
        _counterListArchived.value = list
    }

    private val _counterListLocked = MutableLiveData<MutableList<Counter>>()
    val counterListLocked: LiveData<MutableList<Counter>> get() = _counterListLocked
    fun setCounterListLocked(list: MutableList<Counter>) {
        _counterListLocked.value = list
    }

    private val _counterListLiked = MutableLiveData<MutableList<Counter>>()
    val counterListLiked: LiveData<MutableList<Counter>> get() = _counterListLiked
    fun setCounterListLiked(list: MutableList<Counter>) {
        _counterListLiked.value = list
    }

    val counterListHomeAdapter = CounterListAdapter(counterListHome.value ?: mutableListOf(), this)
    val counterListArchivedAdapter =
        CounterListAdapter(counterListArchived.value ?: mutableListOf(), this)
    val counterListLockedAdapter =
        CounterListAdapter(counterListLocked.value ?: mutableListOf(), this)
    val counterListLikedAdapter =
        CounterListAdapter(counterListLiked.value ?: mutableListOf(), this)

}