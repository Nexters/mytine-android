package com.nexters.mytine.data.repository

import com.nexters.mytine.data.entity.Routine
import kotlinx.coroutines.flow.Flow
import java.time.DayOfWeek
import java.time.LocalDate

internal interface RoutineRepository {
    fun flowRoutinesById(id: String): Flow<List<Routine>>
    fun flowRoutines(date: LocalDate): Flow<List<Routine>>
    fun flowRoutinesByDate(from: LocalDate, to: LocalDate): Flow<List<Routine>>
    suspend fun updateRoutine(emoji: String, name: String, goal: String?, selectedDayOfWeeks: List<DayOfWeek>, id: String = "")
    suspend fun updateStatus(realId: String, status: Routine.Status)
    suspend fun getsByDate(from: LocalDate, to: LocalDate): List<Routine>
    suspend fun deleteRoutinesById(id: String)
}
