package com.nexters.mytine.data.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Transaction
import com.nexters.mytine.data.entity.Routine
import kotlinx.coroutines.flow.Flow
import java.time.DayOfWeek
import java.time.LocalDate

@Dao
internal abstract class RoutineDao : BaseDao<Routine> {
    @Query("SELECT * FROM routine ORDER BY date ASC")
    abstract fun gets(): Flow<List<Routine>>

    @Query("SELECT * FROM routine ORDER BY date ASC")
    abstract suspend fun getsSync(): List<Routine>

    @Query("DELETE FROM routine WHERE date BETWEEN :from AND :to")
    abstract suspend fun deletes(from: LocalDate, to: LocalDate)

    @Transaction
    open suspend fun deleteAndUpdate(entities: List<Routine>) {
        if (entities.isNotEmpty()) {
            val date = entities.first().date
            val from = date.with(DayOfWeek.MONDAY)
            val to = date.with(DayOfWeek.SUNDAY)

            deletes(from, to)
        }
        upserts(entities)
    }
}
