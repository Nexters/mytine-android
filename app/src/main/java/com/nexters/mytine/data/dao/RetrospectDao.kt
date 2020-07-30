package com.nexters.mytine.data.dao

import androidx.room.Dao
import androidx.room.Query
import com.nexters.mytine.data.entity.Retrospect
import kotlinx.coroutines.flow.Flow
import java.time.LocalDate

@Dao
internal abstract class RetrospectDao : BaseDao<Retrospect> {
    @Query("SELECT * FROM retrospect WHERE date=:date")
    abstract suspend fun get(date: LocalDate): Retrospect

    @Query("SELECT * FROM retrospect WHERE date=:date")
    abstract fun getFlow(date: LocalDate): Flow<Retrospect>
}
