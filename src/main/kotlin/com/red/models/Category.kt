package com.red.models

data class Category(
    var id: Int? = null,
    var userId: Int? = null,
    var name: String = "",
    var parent: Int? = null,
    var childrenCategories: List<Int>? = null,
)
