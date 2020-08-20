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

    @Transaction
    open suspend fun updateEmptyRoutines() {
        getLastDate()?.let {
            val now = LocalDate.now()
            val list = getsByDate(it.with(DayOfWeek.MONDAY), it.with(DayOfWeek.SUNDAY))
            var copyDate = it.plusWeeks(1)
            while (copyDate.with(DayOfWeek.MONDAY) <= now.with(DayOfWeek.MONDAY)) {
                upserts(
                    list.map { routine ->
                        routine.run {
                            copy(
                                date = copyDate.with(date.dayOfWeek),
                                status = when (status) {
                                    Routine.Status.SUCCESS, Routine.Status.ENABLE -> Routine.Status.ENABLE
                                    else -> Routine.Status.DISABLE
                                }
                            )
                        }
                    }
                )
                copyDate = copyDate.plusWeeks(1)
            }
        }
    }
}
