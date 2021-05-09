package com.red.models

import java.time.LocalDateTime

data class Goal(
    var id: Int? = null,
    var userId: Int? = null,
    var name: String = "",
    var targetDate: LocalDateTime? = null,
    var creationDate: LocalDateTime = LocalDateTime.now(),
    var goalMoney: Double = 0.0,
    var targetAmount: Double = 0.0,
    var savings: List<GoalSaving>? = null,
)
