package com.rimetech.rimecounter.repositories

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.room.Room
import com.rimetech.rimecounter.data.Counter
import com.rimetech.rimecounter.database.CounterDatabase
import java.util.UUID
import java.util.concurrent.Executors

private const val COUNTER_DATABASE = "counter-database"
class CounterRepository private constructor(context: Context){
    companion object{
        private var INSTANCE:CounterRepository?=null
        fun initialize(context: Context){
            if (INSTANCE==null){
                INSTANCE = CounterRepository(context)
            }
        }
        fun get():CounterRepository{
            return INSTANCE?: throw IllegalArgumentException("CounterRepository is not initialized!")
        }
    }

    private val counterDatabase = Room.databaseBuilder(context.applicationContext, CounterDatabase::class.java,
        COUNTER_DATABASE).build()
    private val counterDao = counterDatabase.counterDao()
    private val executor = Executors.newSingleThreadExecutor()

    fun addCounter(counter: Counter) {
        executor.execute {
            counterDao.addCounter(counter)
        }
    }

    fun deleteCounter(counter: Counter) {
        executor.execute {
            counterDao.deleteCounter(counter)
        }
    }

    fun getCounters(): LiveData<MutableList<Counter>> = counterDao.getCounters()
    fun getCounter(id: UUID): LiveData<Counter> = counterDao.getCounter(id)
    fun searchCounters(keyWord: String): LiveData<MutableList<Counter>> {
        val searchString = "%$keyWord%"
        return counterDao.searchCounters(searchString)
    }

    fun updateCounter(counter: Counter) {
        executor.execute {
            counterDao.updateCounter(counter)
        }
    }

    fun updateCounters(counterList: MutableList<Counter>) {
        executor.execute {
            counterDao.updateCounters(counterList)
        }
    }

    fun removeAllCounters() {
        executor.execute {
            counterDatabase.clearAllTables()
        }
    }


}