package com.nexters.mytine.data

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.nexters.mytine.data.converter.DateConverter
import com.nexters.mytine.data.converter.RoutineConverter
import com.nexters.mytine.data.dao.RetrospectDao
import com.nexters.mytine.data.dao.RoutineDao
import com.nexters.mytine.data.entity.Retrospect
import com.nexters.mytine.data.entity.Routine

@Database(
    entities = [
        Routine::class,
        Retrospect::class
    ],
    version = 1
)
@TypeConverters(
    DateConverter::class,
    RoutineConverter::class
)
internal abstract class MyTineRoomDatabase : RoomDatabase() {
    abstract fun routineDao(): RoutineDao
    abstract fun retrospectDao(): RetrospectDao
}
