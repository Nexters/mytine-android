package com.nexters.mytine.data.repository

import com.nexters.mytine.data.MyTineRoomDatabase
import com.nexters.mytine.data.entity.Retrospect
import kotlinx.coroutines.flow.Flow
import java.time.LocalDate
import javax.inject.Inject

internal class RetrospectRepositoryImpl @Inject constructor(
    private val roomDatabase: MyTineRoomDatabase
) : RetrospectRepository {
    override fun getRetrospect(date: LocalDate): Flow<Retrospect> {
        return roomDatabase.retrospectDao().getFlow(date = date)
    }

    override suspend fun updateRetrospect(retrospect: Retrospect) {
        roomDatabase.retrospectDao().upsert(retrospect)
    }
}
