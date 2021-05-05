package com.red.repository.categories

import com.red.models.Category
import com.red.repository.DatabaseFactory
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.statements.InsertStatement

class CategoryRepository : ICategoriesRepository {

    override suspend fun addCategory(category: Category): Category? {
        var statement: InsertStatement<Number>? = null
        DatabaseFactory.dbQuery {
            statement = Categories.insert {
                it[userId] = category.userId ?: 0
                it[id] = category.id ?: 0
                it[name] = category.name ?: ""
                it[parent] = category.parent
                it[childrenCategories] = category.childrenCategories
            }
        }
        return rowToCategory(statement?.resultedValues?.get(0))
    }


    override suspend fun getCategories(userId: Int): List<Category> {
        return DatabaseFactory.dbQuery {
            Categories.select {
                Categories.userId.eq(userId)
            }.mapNotNull { rowToCategory(it) }
        }
    }

    override suspend fun deleteCategory(userId: Int, categoryId: Int): Boolean {
        val deletedRowNumber = DatabaseFactory.dbQuery {
            Categories.deleteWhere {
                Categories.userId.eq(userId) and Categories.id.eq(categoryId)
            }
        }
        return deletedRowNumber > 0
    }

    override suspend fun updateCategory(userId: Int, category: Category): Boolean {
        val updatedRows = DatabaseFactory.dbQuery {
            Categories.update(
                where = { Categories.userId.eq(userId) and Categories.id.eq(category.id ?: -1) },
                body =
                {
                    it[Categories.userId] = category.userId ?: 0
                    it[id] = category.id ?: 0
                    it[name] = category.name ?: ""
                    it[parent] = category.parent
                    it[childrenCategories] = category.childrenCategories
                })
        }

        return updatedRows > 0
    }

    private fun rowToCategory(row: ResultRow?): Category? {
        if (row == null) {
            return null
        }
        return Category(
            id = row[Categories.id],
            userId = row[Categories.userId],
            name = row[Categories.name],
            parent = row[Categories.parent],
            childrenCategories = row[Categories.childrenCategories],
        )
    }
}