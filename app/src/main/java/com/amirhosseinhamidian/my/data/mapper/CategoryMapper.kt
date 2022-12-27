package com.amirhosseinhamidian.my.data.mapper

import com.amirhosseinhamidian.my.data.db.entity.CategoryEntity
import com.amirhosseinhamidian.my.domain.model.Category


fun CategoryEntity.toCategory(): Category {
    return Category(
        id = id,
        name = name,
    )
}

fun Category.toCategoryEntity(): CategoryEntity {
    return CategoryEntity(
        id = id,
        name = name,
    )
}

fun List<CategoryEntity>.toTaskList() : List<Category> {
    return map {
        it.toCategory()
    }
}

fun List<Category>.toTaskEntityList() : List<CategoryEntity> {
    return map {
        it.toCategoryEntity()
    }
}