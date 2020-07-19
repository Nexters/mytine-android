package com.nexters.mytine.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "routine")
internal data class Routine(
    @PrimaryKey(autoGenerate = true)
    val id: Int,
    val title: String,
    val content: String
)
