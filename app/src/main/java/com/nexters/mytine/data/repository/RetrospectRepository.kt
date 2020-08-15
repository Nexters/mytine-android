package com.nexters.mytine.data.repository

import com.nexters.mytine.data.entity.Retrospect
import kotlinx.coroutines.flow.Flow
import java.time.LocalDate

internal interface RetrospectRepository {
    fun getRetrospect(date: LocalDate): Flow<Retrospect?>
    fun getRetrospectDatesByDate(from: LocalDate, to: LocalDate): Flow<List<LocalDate>>
    suspend fun updateRetrospect(retrospect: Retrospect)
    suspend fun deleteRetrospect(date: LocalDate)
}
