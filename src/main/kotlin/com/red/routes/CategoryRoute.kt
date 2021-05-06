package com.red.routes


import com.red.API_VERSION
import com.red.auth.MySession
import com.red.models.Category
import com.red.repository.categories.CategoryRepository
import com.red.repository.users.UserRepository
import io.ktor.application.*
import io.ktor.auth.*
import io.ktor.http.*
import io.ktor.locations.*
import io.ktor.request.*
import io.ktor.response.*
import io.ktor.routing.*
import io.ktor.sessions.*

const val CATEGORY = "$API_VERSION/category"
const val CATEGORIES = "$API_VERSION/categories"

@KtorExperimentalLocationsAPI
@Location(CATEGORY)
class CategoryRoute

@KtorExperimentalLocationsAPI
@Location(CATEGORIES)
class CategoriesRoute

@KtorExperimentalLocationsAPI
fun Route.categories(categoryRepository: CategoryRepository, userRepository: UserRepository) {
    authenticate("jwt") {
        post<CategoryRoute> {
            val userId = call.getUserId(userRepository) ?: return@post

            val category = call.receive<Category>()
            val validatedCategory = call.validateCategory(category, userId) ?: return@post

            try {
                val currentCategory = categoryRepository.addCategory(validatedCategory)
                currentCategory?.id?.let {
                    call.respond(HttpStatusCode.OK, currentCategory)
                }
            } catch (e: Throwable) {
                application.log.error("Failed to add category", e)
                call.respond(HttpStatusCode.BadRequest, "Problems saving category")
            }
        }

        post<CategoriesRoute> {
            val userId = call.getUserId(userRepository) ?: return@post

            val categories = call.receive<Array<Category>>()
            val savedCategories = ArrayList<Category>()

            categories.forEach { category ->

                val validatedCategory = call.validateCategory(category, userId) ?: return@post

                try {
                    val currentCategory = categoryRepository.addCategory(validatedCategory)
                    if (currentCategory?.id == null) {
                        throw Exception("Failed to save category")
                    } else {
                        savedCategories.add(currentCategory)
                    }
                } catch (e: Throwable) {
                    application.log.error("Failed to add category", e)
                    call.respond(HttpStatusCode.BadRequest, "Problems saving categories")
                }
            }

            call.respond(HttpStatusCode.OK, savedCategories)
        }

        get<CategoriesRoute> {
            val userId = call.getUserId(userRepository) ?: return@get

            try {
                val categories = categoryRepository.getCategories(userId)
                call.respond(categories)
            } catch (e: Throwable) {
                application.log.error("Failed to get categories", e)
                call.respond(HttpStatusCode.BadRequest, "Problems getting categories")
            }
        }

        delete<CategoryRoute> {
            val userId = call.getUserId(userRepository) ?: return@delete

            val params = call.receive<Parameters>()
            val categoryId = params["categoryId"]?.toInt()
            if (categoryId == null) {
                call.respond(HttpStatusCode.BadRequest, "Missing category id")
                return@delete
            }

            try {
                val isSuccessful = categoryRepository.deleteCategory(userId, categoryId)
                if (isSuccessful) {
                    call.respond(HttpStatusCode.OK)
                } else {
                    call.respond(HttpStatusCode.BadRequest, "Cannot delete category")
                }
            } catch (e: Throwable) {
                application.log.error("Failed to delete category", e)
                call.respond(HttpStatusCode.BadRequest, "Problems deleting category")
            }
        }

        put<CategoryRoute> {
            val userId = call.getUserId(userRepository) ?: return@put

            val category = call.receive<Category>()
            val validatedCategory = call.validateCategory(category, userId) ?: return@put

            try {
                val isSuccessful = categoryRepository.updateCategory(userId, validatedCategory)
                if (isSuccessful) {
                    call.respond(HttpStatusCode.OK)
                } else {
                    call.respond(HttpStatusCode.BadRequest, "Problems updating category")
                }
            } catch (e: Throwable) {
                application.log.error("Failed to add category", e)
                call.respond(HttpStatusCode.BadRequest, "Problems updating category")
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

private suspend fun ApplicationCall.validateCategory(category: Category, userId: Int): Category? {
    if (category.id == null) {
        respond(HttpStatusCode.BadRequest, "Missing id")
        return null
    }

    if (category.userId == null) {
        category.userId = userId
    }

    return category
}