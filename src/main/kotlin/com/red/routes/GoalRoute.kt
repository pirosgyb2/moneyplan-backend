package com.red.routes


import com.red.API_VERSION
import com.red.auth.MySession
import com.red.models.Goal
import com.red.repository.goals.GoalRepository
import com.red.repository.users.UserRepository
import io.ktor.application.*
import io.ktor.auth.*
import io.ktor.http.*
import io.ktor.locations.*
import io.ktor.request.*
import io.ktor.response.*
import io.ktor.routing.*
import io.ktor.sessions.*
import java.time.LocalDateTime

const val GOAL = "$API_VERSION/goal"
const val GOALS = "$API_VERSION/goals"

@KtorExperimentalLocationsAPI
@Location(GOAL)
class GoalRoute

@KtorExperimentalLocationsAPI
@Location(GOALS)
class GoalsRoute

@KtorExperimentalLocationsAPI
@Location("$GOAL/{id}")
class GoalDeleteRoute(val id: Int)

@KtorExperimentalLocationsAPI
fun Route.goals(goalRepository: GoalRepository, userRepository: UserRepository) {
    authenticate("jwt") {
        post<GoalRoute> {
            val userId = call.getUserId(userRepository) ?: return@post

            val goal = call.receive<Goal>()
            val validatedGoal = call.validateGoal(goal, userId) ?: return@post

            try {
                val currentGoal = goalRepository.addGoal(validatedGoal)
                currentGoal?.id?.let {
                    call.respond(HttpStatusCode.OK, currentGoal)
                }
            } catch (e: Throwable) {
                application.log.error("Failed to add goal", e)
                call.respond(HttpStatusCode.BadRequest, "Problems Saving goal")
            }
        }

        post<GoalsRoute> {
            val userId = call.getUserId(userRepository) ?: return@post

            val goals = call.receive<Array<Goal>>()
            val savedGoals = ArrayList<Goal>()

            goalRepository.deleteGoals(userId)

            goals.forEach { goal ->

                val validatedGoal = call.validateGoal(goal, userId) ?: return@post

                try {
                    val currentGoal = goalRepository.addGoal(validatedGoal)
                    if (currentGoal?.id == null) {
                        throw Exception("Failed to save goal")
                    } else {
                        savedGoals.add(currentGoal)
                    }
                } catch (e: Throwable) {
                    application.log.error("Failed to add goals", e)
                    call.respond(HttpStatusCode.BadRequest, "Problems Saving goals")
                }
            }

            call.respond(HttpStatusCode.OK, savedGoals)
        }

        get<GoalsRoute> {
            val userId = call.getUserId(userRepository) ?: return@get

            try {
                val goals = goalRepository.getGoals(userId)
                call.respond(goals)
            } catch (e: Throwable) {
                application.log.error("Failed to get goals", e)
                call.respond(HttpStatusCode.BadRequest, "Problems getting goals")
            }
        }

        delete<GoalDeleteRoute> { pathParams ->
            val userId = call.getUserId(userRepository) ?: return@delete
            val goalId = pathParams.id

            try {
                val isSuccessful = goalRepository.deleteGoal(userId, goalId)
                if (isSuccessful) {
                    call.respond(HttpStatusCode.OK)
                } else {
                    call.respond(HttpStatusCode.BadRequest, "Cannot delete goal")
                }
            } catch (e: Throwable) {
                application.log.error("Failed to delete goal", e)
                call.respond(HttpStatusCode.BadRequest, "Problems deleting goals")
            }
        }

        put<GoalRoute> {
            val userId = call.getUserId(userRepository) ?: return@put

            val goal = call.receive<Goal>()
            val validatedGoal = call.validateGoal(goal, userId) ?: return@put

            try {
                val isSuccessful = goalRepository.updateGoal(userId, validatedGoal)
                if (isSuccessful) {
                    call.respond(HttpStatusCode.OK)
                } else {
                    call.respond(HttpStatusCode.BadRequest, "Problems updating goal")
                }
            } catch (e: Throwable) {
                application.log.error("Failed to add goal", e)
                call.respond(HttpStatusCode.BadRequest, "Problems updating goal")
            }
        }

    }
}

private suspend fun ApplicationCall.getUserId(userRepository: UserRepository): Int? {
    val user = sessions.get<MySession>()?.let { userRepository.findUser(it.userId) }
    if (user == null) {
        respond(HttpStatusCode.Unauthorized)
    }
    return user?.userId
}

private suspend fun ApplicationCall.validateGoal(goal: Goal, userId: Int): Goal? {
    if (goal.id == null || goal.id == 0) {
        val date = LocalDateTime.now()
        goal.id =
            "${date.monthValue}${date.dayOfMonth}${date.hour}${date.minute}${date.second}".toInt()
    }

    if (goal.userId == null || goal.userId == -1) {
        goal.userId = userId
    }

    return goal
}
