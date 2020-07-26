package com.nexters.mytine.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.time.LocalDate

@Entity(tableName = "routine")
internal data class Routine(
    val date: LocalDate,
    val emoji: String,
    val name: String,
    val goal: String,
    val status: Status,
    @PrimaryKey
    val id: String = "date=$date,emoji=$emoji,name=$name,goal=$goal"
) {
    enum class Status {
        SUCCESS,
        FAIL
    }
}
