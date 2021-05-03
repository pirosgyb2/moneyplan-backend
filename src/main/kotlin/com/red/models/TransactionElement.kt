package com.red.models

data class TransactionElement(
    var id: Long = System.nanoTime(),
    var userId: Int,
    var name: String = "",
    var cost: Double = 0.0,
    var categories: List<Int> = emptyList()
)
