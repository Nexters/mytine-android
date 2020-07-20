package com.nexters.mytine.data.dao

import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy

internal interface BaseDao<T> {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upserts(entities: List<T>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(entity: T)

    @Delete
    suspend fun delete(entity: T)

    @Delete
    suspend fun deletes(entities: List<T>)
}
