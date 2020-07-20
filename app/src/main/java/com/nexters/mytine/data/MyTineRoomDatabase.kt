package com.nexters.mytine.data

import androidx.room.Database
import androidx.room.RoomDatabase
import com.nexters.mytine.data.dao.RoutineDao
import com.nexters.mytine.data.entity.Routine

@Database(
    entities = [
        Routine::class
    ],
    version = 1
)
internal abstract class MyTineRoomDatabase : RoomDatabase() {
    abstract fun routineDao(): RoutineDao
}
