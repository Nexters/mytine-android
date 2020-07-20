package com.nexters.mytine.data.repository

import com.nexters.mytine.data.MyTineRoomDatabase
import com.nexters.mytine.data.entity.Routine
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

internal class RoutineRepositoryImpl @Inject constructor(
    private val roomDatabase: MyTineRoomDatabase
) : RoutineRepository {
    override fun getRoutines(): Flow<List<Routine>> {
        return roomDatabase.routineDao().gets()
    }

    override suspend fun updateRoutine(routine: Routine) {
        roomDatabase.routineDao().upsert(routine)
    }
}
