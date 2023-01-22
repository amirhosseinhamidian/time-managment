package com.amirhosseinhamidian.my.data.mapper

import com.amirhosseinhamidian.my.data.db.entity.DailyDetailsEntity
import com.amirhosseinhamidian.my.data.db.entity.TaskEntity
import com.amirhosseinhamidian.my.domain.model.DailyDetails
import com.amirhosseinhamidian.my.domain.model.Task


fun DailyDetailsEntity.toDailyDetails(): DailyDetails {
    return DailyDetails(
        id = id,
        taskId = taskId,
        categoryName = categoryName,
        weekNumberOfYear = weekNumberOfYear,
        date = date,
        time = time,
    )
}

fun DailyDetails.toDailyDetailsEntity(): DailyDetailsEntity {
    return DailyDetailsEntity(
        id = id,
        taskId = taskId,
        categoryName = categoryName,
        weekNumberOfYear = weekNumberOfYear,
        date = date,
        time = time,
    )
}

fun List<DailyDetailsEntity>.toDailyDetailsList() : List<DailyDetails> {
    return map {
        it.toDailyDetails()
    }
}

fun List<DailyDetails>.toDailyDetailsEntityList() : List<DailyDetailsEntity> {
    return map {
        it.toDailyDetailsEntity()
    }
}