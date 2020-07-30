package com.nexters.mytine.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.time.LocalDate

@Entity(tableName = "retrospect")
internal data class Retrospect(
    @PrimaryKey
    val date: LocalDate,
    val contents: String
)
