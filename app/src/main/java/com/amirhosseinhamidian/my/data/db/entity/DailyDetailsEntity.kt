package com.amirhosseinhamidian.my.data.db.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "daily_details_table")
data class DailyDetailsEntity(
    @PrimaryKey(autoGenerate = true) val id: Long? = null,
    val taskId: Long,
    val date: String,
    val time: Int
)