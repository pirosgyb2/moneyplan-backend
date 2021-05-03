package com.red.models

data class Category(
    var id: Int,
    var userId: Int,
    var name: String = "",
    var parent: Int? = null,
    var children: List<Int> = emptyList(),
)
