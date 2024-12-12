package com.rimetech.rimecounter.data

import android.net.Uri
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.rimetech.rimecounter.utils.COLOR_RED
import java.util.Date
import java.util.UUID

@Entity
data class Counter(
    @PrimaryKey val id: UUID = UUID.randomUUID(),
    var name: String = "No Name Counter",
    var currentValue: Int=0,
    var resetValue: Int=0,
    var increaseValue:Int=1,
    var decreaseValue:Int=1,
    var color:String= COLOR_RED,
    var autoInSecond:Int=3,
    var startTime: Date = Date(),
    val usageList: MutableList<Pair<Pair<Date,Date>,Int>> = mutableListOf(),
    var isFavored:Boolean=false,
    var isLocked:Boolean=false,
    var isArchived:Boolean=false,
    var autoMediaUri: Uri?,
    var isRunning:Boolean=false,
    var targetValue: Int? = null,
    var targetSeconds: Long? = null,
    var isTargetStarted: Boolean = false,
    var targetStartDate: Date? = null
)
