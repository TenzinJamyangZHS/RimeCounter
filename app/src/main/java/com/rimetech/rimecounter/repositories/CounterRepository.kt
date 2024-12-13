package com.rimetech.rimecounter.repositories

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.room.Room
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
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


    private val MIGRATION_1_2 = object : Migration(1, 2) {
        override fun migrate(db: SupportSQLiteDatabase) {
            db.execSQL("ALTER TABLE Counter ADD COLUMN targetValue INTEGER")
            db.execSQL("ALTER TABLE Counter ADD COLUMN targetCircle INTEGER")
            db.execSQL("ALTER TABLE Counter ADD COLUMN targetSeconds INTEGER")
            db.execSQL("ALTER TABLE Counter ADD COLUMN isTargetStarted INTEGER NOT NULL DEFAULT 0")
            db.execSQL("ALTER TABLE Counter ADD COLUMN targetStartDate INTEGER")
            db.execSQL(
                "ALTER TABLE Counter ADD COLUMN targetList TEXT NOT NULL DEFAULT ''"
            )
        }
    }

    private val MIGRATION_2_3 = object : Migration(2, 3) {
        override fun migrate(db: SupportSQLiteDatabase) {
            db.execSQL(
                """
            CREATE TABLE new_Counter (
                id TEXT NOT NULL PRIMARY KEY,
                name TEXT NOT NULL,
                currentValue INTEGER NOT NULL,
                resetValue INTEGER NOT NULL,
                increaseValue INTEGER NOT NULL,
                decreaseValue INTEGER NOT NULL,
                color TEXT NOT NULL,
                autoInSecond INTEGER NOT NULL,
                startTime INTEGER NOT NULL,
                usageList TEXT NOT NULL,
                isFavored INTEGER NOT NULL,
                isLocked INTEGER NOT NULL,
                isArchived INTEGER NOT NULL,
                autoMediaUri TEXT,
                isRunning INTEGER NOT NULL,
                targetValue INTEGER,
                targetSeconds INTEGER,
                isTargetStarted INTEGER NOT NULL,
                targetStartDate INTEGER
            )
            """.trimIndent()
            )

            db.execSQL(
                """
            INSERT INTO new_Counter (
                id, name, currentValue, resetValue, increaseValue, decreaseValue, color,
                autoInSecond, startTime, usageList, isFavored, isLocked, isArchived,
                autoMediaUri, isRunning, targetValue, targetSeconds, isTargetStarted, targetStartDate
            )
            SELECT 
                id, name, currentValue, resetValue, increaseValue, decreaseValue, color,
                autoInSecond, startTime, usageList, isFavored, isLocked, isArchived,
                autoMediaUri, isRunning, targetValue, targetSeconds, isTargetStarted, targetStartDate
            FROM Counter
            """.trimIndent()
            )

            db.execSQL("DROP TABLE Counter")

            db.execSQL("ALTER TABLE new_Counter RENAME TO Counter")
        }
    }




    private val MIGRATION_3_4 = object : Migration(3, 4) {
        override fun migrate(db: SupportSQLiteDatabase) {
            db.execSQL(
                """
            CREATE TABLE new_Counter (
                id TEXT NOT NULL PRIMARY KEY,
                name TEXT NOT NULL,
                currentValue INTEGER NOT NULL,
                resetValue INTEGER NOT NULL,
                increaseValue INTEGER NOT NULL,
                decreaseValue INTEGER NOT NULL,
                color TEXT NOT NULL,
                autoInSecond INTEGER NOT NULL,
                startTime INTEGER NOT NULL,
                usageList TEXT NOT NULL,
                isFavored INTEGER NOT NULL,
                isLocked INTEGER NOT NULL,
                isArchived INTEGER NOT NULL,
                autoMediaUri TEXT
            )
            """.trimIndent()
            )

            db.execSQL(
                """
            INSERT INTO new_Counter (
                id, name, currentValue, resetValue, increaseValue, decreaseValue, color,
                autoInSecond, startTime, usageList, isFavored, isLocked, isArchived,
                autoMediaUri
            )
            SELECT 
                id, name, currentValue, resetValue, increaseValue, decreaseValue, color,
                autoInSecond, startTime, usageList, isFavored, isLocked, isArchived,
                autoMediaUri
            FROM Counter
            """.trimIndent()
            )

            db.execSQL("DROP TABLE Counter")

            db.execSQL("ALTER TABLE new_Counter RENAME TO Counter")
        }
    }



    private val counterDatabase = Room.databaseBuilder(context.applicationContext, CounterDatabase::class.java,
        COUNTER_DATABASE).addMigrations(MIGRATION_1_2,MIGRATION_2_3,MIGRATION_3_4).build()
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