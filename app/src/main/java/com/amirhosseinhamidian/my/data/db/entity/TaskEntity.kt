package com.amirhosseinhamidian.my.data.db.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey


@Entity(tableName = "task_table")
data class TaskEntity(
    @PrimaryKey(autoGenerate = true) val id: Long? = null,
    val title: String,
    @ColumnInfo(name = "description", defaultValue = "")
    val description: String? = null,
    val category: String,
    val elapsedTime: Int
)