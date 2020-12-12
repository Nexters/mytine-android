package com.nexters.mytine.data.repository

import android.content.SharedPreferences
import com.nexters.mytine.data.dao.RoutineDao
import com.nexters.mytine.data.entity.Routine
import kotlinx.coroutines.flow.Flow
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import javax.inject.Inject

internal class RoutineRepositoryImpl @Inject constructor(
    private val routineDao: RoutineDao,
    private val sharedPreferences: SharedPreferences

) : RoutineRepository {

    override fun flowRoutinesById(id: String, startDate: LocalDate): Flow<List<Routine>> {
        return routineDao.flowRoutinesById(id, startDate)
    }

    override fun flowRoutines(date: LocalDate): Flow<List<Routine>> {
        return routineDao.flowRoutines(date)
    }

    override fun flowRoutinesByDate(from: LocalDate, to: LocalDate): Flow<List<Routine>> {
        return routineDao.flowRoutineByDate(from, to)
    }

    override suspend fun getsStartDate(): LocalDate? {
        return routineDao.getStartDate()
    }

    override suspend fun updateRoutine(
        emoji: String,
        name: String,
        goal: String?,
        selectedDayOfWeeks: List<DayOfWeek>,
        id: String
    ) {
        val isUpdate = id.isNotBlank()
        val now = LocalDate.now()

        val order = if (isUpdate) {
            routineDao.getsById(id).first().order
        } else {
            routineDao.getsByDate(now.with(DayOfWeek.MONDAY), now.with(DayOfWeek.SUNDAY)).distinctBy { it.id }.size
        }

        val routines = DayOfWeek.values().map {
            val status = if (selectedDayOfWeeks.contains(it)) {
                Routine.Status.ENABLE
            } else {
                Routine.Status.DISABLE
            }

            Routine(
                date = now.with(it),
                emoji = emoji,
                name = name,
                goal = goal,
                status = status,
                order = order
            )
        }

        if (isUpdate) {
            routineDao.deleteAndUpdate(id, now.with(DayOfWeek.MONDAY), routines)
        } else {
            routineDao.upserts(routines)
        }
    }

    override suspend fun updateStatus(realId: String, status: Routine.Status) {
        return routineDao.updateStatus(realId, status)
    }

    override suspend fun getsByDate(from: LocalDate, to: LocalDate): List<Routine> {
        return routineDao.getsByDate(from, to)
    }

    override suspend fun deleteRoutinesById(id: String) {
        return routineDao.deleteRoutinesById(id, LocalDate.now().with(DayOfWeek.MONDAY))
    }

    override suspend fun updateEmptyRoutines() {
        val now = LocalDate.now()
        val lastUpdatedDate = LocalDate.parse(sharedPreferences.getString(KEY_LAST_UPDATED_DATE, DateTimeFormatter.ISO_LOCAL_DATE.format(now)))

        routineDao.getLastDate()?.let {
            val list = getsByDate(it.with(DayOfWeek.MONDAY), it.with(DayOfWeek.SUNDAY))
            var copyDate = it.plusWeeks(1)
            while (copyDate.with(DayOfWeek.MONDAY) <= now.with(DayOfWeek.MONDAY) && lastUpdatedDate <= copyDate) {
                routineDao.upserts(
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
            sharedPreferences.edit().putString(KEY_LAST_UPDATED_DATE, DateTimeFormatter.ISO_LOCAL_DATE.format(copyDate)).commit()
        }
    }

    override suspend fun getRoutinesByStatus(to: LocalDate, from: LocalDate, status: Routine.Status): List<Routine> {
        return routineDao.getRoutinesByStatus(to, from, status)
    }

    companion object {
        const val KEY_LAST_UPDATED_DATE = "keyLastUpdatedDate"
    }
}
