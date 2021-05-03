package com.red.models

import java.io.Serializable
import java.time.LocalDateTime

data class Goal(
    var id: Int = 0,
    var userId: Int,
    var name: String = "",
    var targetDate: LocalDateTime? = null,
    var creationDate: LocalDateTime = LocalDateTime.now(),
    var goalMoney: Double = 0.0,
    var targetAmount: Double = 0.0,
    var savings: List<GoalSaving> = emptyList(),
) : Serializable
