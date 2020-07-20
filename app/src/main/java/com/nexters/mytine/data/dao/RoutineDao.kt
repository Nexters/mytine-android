package com.nexters.mytine.data.dao

import androidx.room.Dao
import androidx.room.Query
import com.nexters.mytine.data.entity.Routine
import kotlinx.coroutines.flow.Flow

@Dao
internal abstract class RoutineDao : BaseDao<Routine> {
    @Query("SELECT * FROM routine")
    abstract fun gets(): Flow<List<Routine>>
}
