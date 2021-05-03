package com.red.repository

import com.red.util.array
import org.jetbrains.exposed.sql.Column
import org.jetbrains.exposed.sql.IntegerColumnType
import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.datetime

object Transactions : Table() {
    val id: Column<Int> = integer("id").autoIncrement().primaryKey()
    val userId: Column<Int> = integer("userId").references(Users.userId)
    val name = varchar("name", 256)
    val totalCost = double("totalCost")
    val currency = varchar("currency", 256)
    val date = datetime("date")
    val categories = array<Int>("categories", IntegerColumnType())
    val elements = array<Int>("elements", IntegerColumnType())
    val type = varchar("type", 256)
}