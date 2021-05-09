package com.red.repository.goals

import com.red.repository.users.Users
import org.jetbrains.exposed.sql.Column
import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.datetime

object Goals : Table() {
    val primaryKey = varchar("primaryKey", 256).primaryKey()
    val id: Column<Int> = integer("id")
    val userId: Column<Int> = integer("userId").references(Users.userId)
    val name = varchar("name", 256)
    val targetDate = datetime("date").nullable()
    val creationDate = datetime("creationDate")
    val goalMoney = double("goalMoney")
    val targetAmount = double("targetAmount")
    val savings = varchar("savings", 10485760).nullable()
}