package com.rimetech.rimecounter.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.rimetech.rimecounter.data.Counter

@Database(entities = [Counter::class], version = 4, exportSchema = false)
@TypeConverters(CounterTypeConverters::class)
abstract class CounterDatabase: RoomDatabase() {
    abstract fun counterDao():CounterDao
}