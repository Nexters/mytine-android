package com.nexters.mytine.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.time.LocalDate

@Entity(tableName = "routine")
internal data class Routine(
    val date: LocalDate,
    val emoji: String,
    val name: String,
    val goal: String?,
    val status: Status,
    val order: Int,
    val id: String = "emoji=$emoji,name=$name,goal=$goal,order=$order",
    @PrimaryKey
    val realId: String = "date=$date,emoji=$emoji,name=$name,goal=$goal,order=$order"
) {
    enum class Status {
        SUCCESS,
        FAIL,
        ENABLE,
        DISABLE
    }

    @SuppressWarnings("LongParameterList")
    fun copy(
        date: LocalDate = this.date,
        emoji: String = this.emoji,
        name: String = this.name,
        goal: String? = this.goal,
        status: Status = this.status,
        order: Int = this.order
    ): Routine {
        return Routine(date, emoji, name, goal, status, order)
    }
}
