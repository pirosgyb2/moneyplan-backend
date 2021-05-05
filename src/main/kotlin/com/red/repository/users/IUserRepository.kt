package com.red.repository.users

import com.red.models.User

interface IUserRepository {

    suspend fun addUser(email: String, displayName: String, passwordHash: String): User?
    suspend fun findUser(userId: Int): User?
    suspend fun findUserByEmail(email: String): User?

}