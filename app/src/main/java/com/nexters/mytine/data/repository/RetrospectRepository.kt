package com.nexters.mytine.data.repository

import com.nexters.mytine.data.entity.Retrospect
import kotlinx.coroutines.flow.Flow

internal interface RetrospectRepository {
    fun getRetrospect(): Flow<Retrospect>
    suspend fun updateRetrospect(content: String)
}
