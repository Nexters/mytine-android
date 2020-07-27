package com.nexters.mytine.data.repository

import com.nexters.mytine.data.entity.Routine
import kotlinx.coroutines.flow.Flow
import java.time.DayOfWeek

internal interface RoutineRepository {
    fun getRoutines(): Flow<List<Routine>>
    suspend fun updateRoutine(emoji: String, name: String, goal: String, selectedDayOfWeeks: List<DayOfWeek>, id: String = "")
}
