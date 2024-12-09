package com.rimetech.rimecounter.database

import android.net.Uri
import androidx.room.TypeConverter
import androidx.room.TypeConverters
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.util.Date
import java.util.UUID

@TypeConverters
class CounterTypeConverters {
    private val gson = Gson()

    @TypeConverter
    fun fromUUID(uuid: UUID): String {
        return uuid.toString()
    }

    @TypeConverter
    fun toUUID(string: String): UUID {
        return UUID.fromString(string)
    }

    @TypeConverter
    fun fromDate(date: Date?): Long? {
        return date?.time
    }

    @TypeConverter
    fun toDate(long: Long?): Date? {
        return long?.let { Date(it) }
    }

    @TypeConverter
    fun fromUri(uri: Uri): String {
        return uri.toString()
    }

    @TypeConverter
    fun toUri(string: String): Uri {
        return Uri.parse(string)
    }

    @TypeConverter
    fun fromList(value: MutableList<Pair<Pair<Date, Date>, Int>>?): String? {
        return gson.toJson(value)
    }

    @TypeConverter
    fun toList(value: String?): MutableList<Pair<Pair<Date, Date>, Int>>? {
        val listType = object : TypeToken<MutableList<Pair<Pair<Date, Date>, Int>>>() {}.type
        return gson.fromJson(value, listType)
    }
}