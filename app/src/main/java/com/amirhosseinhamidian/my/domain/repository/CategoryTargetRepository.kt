package com.amirhosseinhamidian.my.domain.repository

import com.amirhosseinhamidian.my.domain.model.CategoryTarget

interface CategoryTargetRepository {
    suspend fun insertCategoryTarget(categoryTarget: CategoryTarget): Long
    suspend fun updateCategoryTarget(categoryTarget: CategoryTarget)
    suspend fun deleteCategoryTarget(categoryTarget: CategoryTarget)
    suspend fun getWeekCategoryTarget(startDayWeek: String): List<CategoryTarget>
}