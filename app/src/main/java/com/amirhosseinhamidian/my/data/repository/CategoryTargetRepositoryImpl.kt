package com.amirhosseinhamidian.my.data.repository

import com.amirhosseinhamidian.my.data.db.DB
import com.amirhosseinhamidian.my.data.mapper.toCategoryTargetEntity
import com.amirhosseinhamidian.my.data.mapper.toCategoryTargetList
import com.amirhosseinhamidian.my.domain.model.CategoryTarget
import com.amirhosseinhamidian.my.domain.repository.CategoryTargetRepository
import javax.inject.Inject

class CategoryTargetRepositoryImpl @Inject constructor(
    db: DB
): CategoryTargetRepository {
    private val dao = db.categoryTargetDao

    override suspend fun insertCategoryTarget(categoryTarget: CategoryTarget): Long {
        return dao.insert(categoryTarget.toCategoryTargetEntity())
    }

    override suspend fun updateCategoryTarget(categoryTarget: CategoryTarget) {
        return dao.update(categoryTarget.toCategoryTargetEntity())
    }

    override suspend fun deleteCategoryTarget(categoryTarget: CategoryTarget) {
        return dao.delete(categoryTarget.toCategoryTargetEntity())
    }

    override suspend fun getWeekCategoryTarget(startDayWeek: String): List<CategoryTarget> {
        return dao.getWeekCategoryTarget(startDayWeek).toCategoryTargetList()
    }
}