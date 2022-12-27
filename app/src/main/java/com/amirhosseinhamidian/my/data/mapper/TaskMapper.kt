package com.amirhosseinhamidian.my.data.mapper

import com.amirhosseinhamidian.my.data.db.entity.TaskEntity
import com.amirhosseinhamidian.my.domain.model.Task


fun TaskEntity.toTask(): Task {
    return Task(
        id = id,
        title = title,
        category = category,
        elapsedTime = elapsedTime
    )
}

fun Task.toTaskEntity(): TaskEntity {
    return TaskEntity(
        id = id,
        title = title,
        category = category,
        elapsedTime = elapsedTime
    )
}

fun List<TaskEntity>.toTaskList() : List<Task> {
    return map {
        it.toTask()
    }
}

fun List<Task>.toTaskEntityList() : List<TaskEntity> {
    return map {
        it.toTaskEntity()
    }
}