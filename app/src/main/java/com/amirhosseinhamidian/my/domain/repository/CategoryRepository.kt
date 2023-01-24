package com.amirhosseinhamidian.my.domain.repository

import com.amirhosseinhamidian.my.domain.model.Category

interface CategoryRepository {
    suspend fun insertCategory(category: Category): Long
    suspend fun updateCategory(category: Category)
    suspend fun deleteCategory(category: Category)
    suspend fun getAllCategory(): List<Category>
    suspend fun getCategoryById(id: Long): Category
    suspend fun getCategoryByName(name: String): Category
    suspend fun isThereCategory(name: String): Boolean
}