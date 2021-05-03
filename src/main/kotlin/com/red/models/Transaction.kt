package com.red.models

import java.time.LocalDateTime

data class Transaction(
    var id: Int = 0,
    var userId: Int,
    var name: String = "",
    var totalCost: Double = 0.0,
    var currency: String = "HUF",
    var date: LocalDateTime = LocalDateTime.MIN,
    var categories: List<Int> = emptyList(),
    var elements: List<TransactionElement> = emptyList(),
    var type: String
)
