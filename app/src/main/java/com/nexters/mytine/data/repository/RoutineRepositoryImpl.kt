package com.nexters.mytine.data.repository

import com.nexters.mytine.data.dao.RoutineDao
import com.nexters.mytine.data.entity.Routine
import kotlinx.coroutines.flow.Flow
import java.time.DayOfWeek
import java.time.LocalDate
import javax.inject.Inject

internal class RoutineRepositoryImpl @Inject constructor(
    private val routineDao: RoutineDao

) : RoutineRepository {

    override fun flowRoutines(date: LocalDate): Flow<List<Routine>> {
        return routineDao.flowRoutines(date)
    }

    override fun flowRoutinesByDate(from: LocalDate, to: LocalDate): Flow<List<Routine>> {
        return routineDao.flowRoutineByDate(from, to)
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
            routineDao.deleteAndUpdate(id, routines)
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
}
