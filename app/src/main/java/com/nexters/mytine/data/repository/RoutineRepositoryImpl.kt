package com.nexters.mytine.data.repository

import com.nexters.mytine.data.MyTineRoomDatabase
import com.nexters.mytine.data.entity.Routine
import kotlinx.coroutines.flow.Flow
import java.time.DayOfWeek
import java.time.LocalDate
import javax.inject.Inject

internal class RoutineRepositoryImpl @Inject constructor(
    private val roomDatabase: MyTineRoomDatabase
) : RoutineRepository {
    override fun getRoutines(): Flow<List<Routine>> {
        return roomDatabase.routineDao().gets()
    }

    override suspend fun addRoutine(emoji: String, name: String, goal: String, selectedDayOfWeeks: List<DayOfWeek>) {
        val now = LocalDate.now()

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
                status = status
            )
        }

        roomDatabase.routineDao().deleteAndUpdate(routines)
    }
}
