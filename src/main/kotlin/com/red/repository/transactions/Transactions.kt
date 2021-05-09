package com.red.repository.transactions

import com.red.repository.users.Users
import com.red.util.array
import org.jetbrains.exposed.sql.Column
import org.jetbrains.exposed.sql.IntegerColumnType
import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.datetime

object Transactions : Table() {
    val primaryKey = varchar("primaryKey", 256).primaryKey()
    val id: Column<Int> = integer("id")
    val userId: Column<Int> = integer("userId").references(Users.userId)
    val name = varchar("name", 256)
    val totalCost = double("totalCost")
    val currency = varchar("currency", 256)
    val date = datetime("date")
    val categories: Column<Array<Int>?> = array<Int>("categories", IntegerColumnType()).nullable()
    val elements = varchar("elements", 10485760).nullable()
    val type = varchar("type", 256)
}