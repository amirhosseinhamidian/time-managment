package com.amirhosseinhamidian.my.domain.repository

import com.amirhosseinhamidian.my.domain.model.Category

interface CategoryRepository {
    suspend fun insertCategory(category: Category): Long
    suspend fun deleteCategory(category: Category)
    suspend fun getAllCategory(): List<Category>
    suspend fun getCategoryById(id: Long): Category
}