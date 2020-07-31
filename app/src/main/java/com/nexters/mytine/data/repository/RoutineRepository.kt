package com.nexters.mytine.data.repository

import com.nexters.mytine.data.entity.Routine
import kotlinx.coroutines.flow.Flow
import java.time.DayOfWeek
import java.time.LocalDate

internal interface RoutineRepository {
    fun flowRoutines(date: LocalDate): Flow<List<Routine>>
    fun flowRoutinesByDate(from: LocalDate, to: LocalDate): Flow<List<Routine>>
    suspend fun updateRoutine(emoji: String, name: String, goal: String?, selectedDayOfWeeks: List<DayOfWeek>, id: String = "")
    suspend fun getsByDate(from: LocalDate, to: LocalDate): List<Routine>
}
