package com.nexters.mytine.data.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Transaction
import com.nexters.mytine.data.entity.Routine
import kotlinx.coroutines.flow.Flow
import java.time.LocalDate

@Dao
internal abstract class RoutineDao : BaseDao<Routine> {
    @Query("SELECT * FROM routine WHERE id = :id AND date >= :startDate")
    abstract fun flowRoutinesById(id: String, startDate: LocalDate): Flow<List<Routine>>

    @Query("SELECT * FROM routine WHERE date = :date")
    abstract fun flowRoutines(date: LocalDate): Flow<List<Routine>>

    @Query("SELECT * FROM routine WHERE date BETWEEN :from AND :to ORDER BY date")
    abstract fun flowRoutineByDate(from: LocalDate, to: LocalDate): Flow<List<Routine>>

    @Query("SELECT * FROM routine ORDER BY date ASC")
    abstract suspend fun gets(): List<Routine>

    @Query("SELECT * FROM routine WHERE id = :id")
    abstract suspend fun getsById(id: String): List<Routine>

    @Query("SELECT * FROM routine WHERE date BETWEEN :from AND :to ORDER BY date")
    abstract suspend fun getsByDate(from: LocalDate, to: LocalDate): List<Routine>

    @Query("SELECT date FROM routine ORDER BY date LIMIT 1")
    abstract suspend fun getStartDate(): LocalDate?

    @Query("SELECT date FROM routine ORDER BY date DESC LIMIT 1")
    abstract suspend fun getLastDate(): LocalDate?

    @Query("DELETE FROM routine WHERE id = :id AND date >= :startDate")
    abstract suspend fun deleteRoutinesById(id: String, startDate: LocalDate)

    @Query("UPDATE routine SET status = :status WHERE realId = :id")
    abstract suspend fun updateStatus(id: String, status: Routine.Status)

    @Transaction
    open suspend fun deleteAndUpdate(id: String, startDate: LocalDate, entities: List<Routine>) {
        deleteRoutinesById(id, startDate)
        upserts(entities)
    }

    @Query("SELECT * FROM routine WHERE :to <= date AND date <= :from AND status = :status")
    abstract suspend fun getRoutinesByStatus(to: LocalDate, from: LocalDate, status: Routine.Status): List<Routine>
}
