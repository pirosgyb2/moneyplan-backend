package com.red.repository.goals

import com.red.models.Goal


interface IGoalRepository {

    suspend fun addGoal(goal: Goal): Goal?
    suspend fun getGoals(userId: Int): List<Goal>
    suspend fun deleteGoal(userId: Int, goalId: Int): Boolean
    suspend fun updateGoal(userId: Int, goal: Goal): Boolean

}