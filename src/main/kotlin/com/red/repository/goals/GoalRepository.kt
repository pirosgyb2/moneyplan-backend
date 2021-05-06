package com.red.repository.goals

import com.red.models.Goal
import com.red.repository.DatabaseFactory.dbQuery
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.statements.InsertStatement
import java.time.LocalDateTime

class GoalRepository : IGoalRepository {

    override suspend fun addGoal(goal: Goal): Goal? {
        var statement: InsertStatement<Number>? = null
        dbQuery {
            statement = Goals.insert {
                it[Goals.userId] = goal.userId ?: 0
                it[Goals.id] = goal.id ?: 0
                it[Goals.name] = goal.name ?: ""
                it[Goals.targetDate] = goal.targetDate
                it[Goals.creationDate] = goal.creationDate ?: LocalDateTime.now()
                it[Goals.goalMoney] = goal.goalMoney ?: 0.0
                it[Goals.targetAmount] = goal.targetAmount ?: 0.0
                it[Goals.savings] = goal.savings

            }
        }
        return rowToGoal(statement?.resultedValues?.get(0))
    }


    override suspend fun getGoals(userId: Int): List<Goal> {
        return dbQuery {
            Goals.select {
                Goals.userId.eq(userId)
            }.mapNotNull { rowToGoal(it) }
        }
    }

    override suspend fun deleteGoal(userId: Int, goalId: Int): Boolean {
        val deletedRowNumber = dbQuery {
            Goals.deleteWhere {
                Goals.userId.eq(userId) and Goals.id.eq(goalId)
            }
        }
        return deletedRowNumber > 0
    }

    override suspend fun updateGoal(userId: Int, goal: Goal): Boolean {

        val updatedRows = dbQuery {
            Goals.update(
                where = { Goals.userId.eq(userId) and Goals.id.eq(goal.id ?: -1) },
                body =
                {
                    it[Goals.name] = goal.name ?: ""
                    it[Goals.targetDate] = goal.targetDate
                    it[Goals.creationDate] = goal.creationDate ?: LocalDateTime.now()
                    it[Goals.goalMoney] = goal.goalMoney ?: 0.0
                    it[Goals.targetAmount] = goal.targetAmount ?: 0.0
                    it[Goals.savings] = goal.savings
                })
        }

        return updatedRows > 0
    }

    private fun rowToGoal(row: ResultRow?): Goal? {
        if (row == null) {
            return null
        }
        return Goal(
            id = row[Goals.id],
            userId = row[Goals.userId],
            name = row[Goals.name],
            targetDate = row[Goals.targetDate],
            creationDate = row[Goals.creationDate],
            goalMoney = row[Goals.goalMoney],
            targetAmount = row[Goals.targetAmount],
            savings = row[Goals.savings],
        )
    }

}