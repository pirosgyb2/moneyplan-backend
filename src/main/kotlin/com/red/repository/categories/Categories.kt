package com.red.repository.categories

import com.red.repository.users.Users
import com.red.util.array
import org.jetbrains.exposed.sql.Column
import org.jetbrains.exposed.sql.IntegerColumnType
import org.jetbrains.exposed.sql.Table

object Categories : Table() {
    val id: Column<Int> = integer("id").primaryKey()
    val userId: Column<Int> = integer("userId").references(Users.userId)
    val name = varchar("name", 256)
    val parent: Column<Int?> = integer("parent").nullable()
    val childrenCategories: Column<Array<Int>?> = array<Int>("childrenCategories", IntegerColumnType()).nullable()
}