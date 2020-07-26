package com.nexters.mytine.data.converter

import androidx.room.TypeConverter
import com.nexters.mytine.data.entity.Routine

internal class RoutineConverter {
    @TypeConverter
    fun toStatus(value: String): Routine.Status {
        return Routine.Status.valueOf(value)
    }

    @TypeConverter
    fun fromStatus(value: Routine.Status): String {
        return value.name
    }
}
