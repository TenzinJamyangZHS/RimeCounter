package com.rimetech.rimecounter.database

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.rimetech.rimecounter.data.Counter
import java.util.UUID

@Dao
interface CounterDao {
    @Insert
    fun addCounter(counter: Counter)

    @Delete
    fun deleteCounter(counter: Counter)

    @Update
    fun updateCounter(counter: Counter)

    @Update
    fun updateCounters(counterList: MutableList<Counter>)

    @Query("SELECT * FROM Counter WHERE id = :id")
    fun getCounter(id: UUID): LiveData<Counter>

    @Query("SELECT * FROM Counter")
    fun getCounters(): LiveData<MutableList<Counter>>

    @Query("SELECT * FROM Counter WHERE name LIKE :string")
    fun searchCounters(string: String): LiveData<MutableList<Counter>>
}