package com.nexters.mytine.data.dao

import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy

internal interface BaseDao<T> {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun upserts(entities: List<T>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun upsert(entity: T)

    @Delete
    fun delete(entity: T)

    @Delete
    fun deletes(entities: List<T>)
}
