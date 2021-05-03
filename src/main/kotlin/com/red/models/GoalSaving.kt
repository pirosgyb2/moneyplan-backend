package com.red.models

import java.time.LocalDateTime

data class GoalSaving(
    var id: Long = System.nanoTime(),
    var userId: Int,
    var goal: Int = 0,
    var amount: Double = 0.0,
    var date: LocalDateTime = LocalDateTime.now(),
)