package com.amirhosseinhamidian.my.data.repository


import com.example.myapplication.data.db.DB
import com.amirhosseinhamidian.my.data.mapper.toCategory
import com.amirhosseinhamidian.my.data.mapper.toCategoryEntity
import com.amirhosseinhamidian.my.domain.model.Category
import com.amirhosseinhamidian.my.domain.repository.CategoryRepository
import javax.inject.Inject

class CategoryRepositoryImpl @Inject  constructor(
    db: DB
): CategoryRepository {
    private val dao = db.categoryDao

    override suspend fun insertCategory(category: Category) : Long {
        return dao.insert(category.toCategoryEntity())
    }

    override suspend fun deleteCategory(category: Category) {
        return dao.delete(category.toCategoryEntity())
    }

    override suspend fun getAllCategory(): List<Category> {
        return dao.getAllCategory().map { it.toCategory() }
    }

    override suspend fun getCategoryById(id: Long): Category {
        return dao.getCategoryById(id).toCategory()
    }
}