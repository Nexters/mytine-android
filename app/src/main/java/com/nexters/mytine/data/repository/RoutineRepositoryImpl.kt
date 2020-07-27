package com.nexters.mytine.data.repository

import com.nexters.mytine.data.MyTineRoomDatabase
import com.nexters.mytine.data.entity.Routine
import kotlinx.coroutines.flow.Flow
import java.time.DayOfWeek
import java.time.LocalDate
import javax.inject.Inject

internal class RoutineRepositoryImpl @Inject constructor(
    roomDatabase: MyTineRoomDatabase
) : RoutineRepository {

    private val routineDao = roomDatabase.routineDao()

    override fun getRoutines(): Flow<List<Routine>> {
        return routineDao.flowRoutines()
    }

    override suspend fun updateRoutine(
        emoji: String,
        name: String,
        goal: String,
        selectedDayOfWeeks: List<DayOfWeek>,
        id: String
    ) {
        val isUpdate = id.isNotBlank()
        val now = LocalDate.now()

        val order = if (isUpdate) {
            routineDao.getsById(id).first().order
        } else {
            routineDao.getWeekRoutines(now.with(DayOfWeek.MONDAY), now.with(DayOfWeek.SUNDAY)).distinctBy { it.id }.size
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
}
