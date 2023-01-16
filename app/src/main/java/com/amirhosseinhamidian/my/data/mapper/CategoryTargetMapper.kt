package com.amirhosseinhamidian.my.data.mapper

import com.amirhosseinhamidian.my.data.db.entity.CategoryTargetEntity
import com.amirhosseinhamidian.my.domain.model.CategoryTarget


fun CategoryTargetEntity.toCategoryTarget(): CategoryTarget {
    return CategoryTarget(
        id = id,
        category = category.toCategory(),
        hourTarget = hourTarget,
        minuteTarget = minuteTarget,
        totalTimeTargetInSec = totalTimeTargetInSec,
        startDateTarget = startDateTarget,
        endDateTarget = endDateTarget,
        weekNumber = weekNumber,
        createAt = createAt
    )
}

fun CategoryTarget.toCategoryTargetEntity(): CategoryTargetEntity {
    return CategoryTargetEntity(
        id = id,
        category = category.toCategoryEntity(),
        hourTarget = hourTarget,
        minuteTarget = minuteTarget,
        totalTimeTargetInSec = totalTimeTargetInSec,
        startDateTarget = startDateTarget,
        endDateTarget = endDateTarget,
        weekNumber = weekNumber,
        createAt = createAt
    )
}

fun List<CategoryTargetEntity>.toCategoryTargetList() : List<CategoryTarget> {
    return map {
        it.toCategoryTarget()
    }
}

fun List<CategoryTarget>.toCategoryTargetEntityList() : List<CategoryTargetEntity> {
    return map {
        it.toCategoryTargetEntity()
    }
}