package com.nexters.mytine.data.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Transaction
import com.nexters.mytine.data.entity.Routine
import kotlinx.coroutines.flow.Flow
import java.time.LocalDate

@Dao
internal abstract class RoutineDao : BaseDao<Routine> {
    @Query("SELECT * FROM routine WHERE date = :date")
    abstract fun flowRoutines(date: LocalDate): Flow<List<Routine>>

    @Query("SELECT * FROM routine ORDER BY date ASC")
    abstract suspend fun gets(): List<Routine>

    @Query("SELECT * FROM routine WHERE id = :id")
    abstract fun getsById(id: String): List<Routine>

    @Query("SELECT * FROM routine WHERE date BETWEEN :from AND :to")
    abstract suspend fun getsByDate(from: LocalDate, to: LocalDate): List<Routine>

    @Query("DELETE FROM routine WHERE id = :id")
    abstract suspend fun deletes(id: String)

    @Transaction
    open suspend fun deleteAndUpdate(id: String, entities: List<Routine>) {
        deletes(id)
        upserts(entities)
    }
}
