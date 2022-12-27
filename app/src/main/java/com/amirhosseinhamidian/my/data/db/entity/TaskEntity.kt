package com.amirhosseinhamidian.my.data.db.entity

import androidx.room.Entity
import androidx.room.PrimaryKey


@Entity(tableName = "task_table")
data class TaskEntity(
    @PrimaryKey(autoGenerate = true) val id: Long? = null,
    val title: String,
    val category: String,
    val elapsedTime: Int
)