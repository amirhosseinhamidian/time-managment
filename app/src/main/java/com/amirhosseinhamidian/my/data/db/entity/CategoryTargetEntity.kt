package com.amirhosseinhamidian.my.data.db.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.io.Serializable

@Entity(tableName = "category_target_table")
data class CategoryTargetEntity(
    @PrimaryKey(autoGenerate = true) val id: Long? = null,
    val category: CategoryEntity,
    val hourTarget: Int,
    val minuteTarget: Int,
    @ColumnInfo(name = "totalTimeTargetInSec")
    val totalTimeTargetInSec:Int? = null,
    val startDateTarget: String,
    val endDateTarget: String,
    val weekNumber: Int,
    val createAt: Long? = null
)
