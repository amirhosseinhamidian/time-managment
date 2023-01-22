package com.amirhosseinhamidian.my.data.db.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "daily_details_table")
data class DailyDetailsEntity(
    @PrimaryKey(autoGenerate = true) val id: Long? = null,
    val taskId: Long,
    @ColumnInfo(name = "categoryName" , defaultValue = "")
    val categoryName: String? = null,
    @ColumnInfo(name = "weekNumberOfYear", defaultValue = "")
    val weekNumberOfYear: String? = null,
    val date: String,
    val time: Int
)