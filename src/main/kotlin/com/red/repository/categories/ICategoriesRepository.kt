package com.red.repository.categories

import com.red.models.Category

interface ICategoriesRepository {

    suspend fun addCategory(category: Category): Category?
    suspend fun getCategories(userId: Int): List<Category>
    suspend fun deleteCategory(userId: Int, categoryId: Int): Boolean
    suspend fun deleteCategories(userId: Int): Boolean
    suspend fun updateCategory(userId: Int, category: Category): Boolean

}