package com.amirhosseinhamidian.my.data.db.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.amirhosseinhamidian.my.utils.Constants


@Entity(tableName = "task_table")
data class TaskEntity(
    @PrimaryKey(autoGenerate = true) val id: Long? = null,
    val title: String,
    @ColumnInfo(name = "description", defaultValue = "")
    val description: String? = null,
    @ColumnInfo(name = "task_status")
    val taskStatus: Int? = 0,
    val category: String,
    val elapsedTime: Int
)