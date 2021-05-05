package com.red.repository.goals

import com.red.models.GoalSaving
import com.red.repository.users.Users
import com.red.util.array
import org.jetbrains.exposed.sql.Column
import org.jetbrains.exposed.sql.IntegerColumnType
import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.datetime

object Goals : Table() {
    val id: Column<Int> = integer("id").primaryKey()
    val userId: Column<Int> = integer("userId").references(Users.userId)
    val name = varchar("name", 256)
    val targetDate = datetime("date").nullable()
    val creationDate = datetime("creationDate")
    val goalMoney = double("goalMoney")
    val targetAmount = double("targetAmount")
    val savings: Column<Array<GoalSaving>?> = array<GoalSaving>("savings", IntegerColumnType()).nullable()
}