package com.nexters.mytine.data.repository

import com.nexters.mytine.data.entity.Routine
import kotlinx.coroutines.flow.Flow

internal interface RoutineRepository {
    fun getRoutines(): Flow<List<Routine>>
    suspend fun updateRoutine(routine: Routine)
}
