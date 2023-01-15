package com.amirhosseinhamidian.my.data.db.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "category_table")
data class CategoryEntity(
    @PrimaryKey(autoGenerate = true) val id: Long? = null,
    val name: String,
    @ColumnInfo(name = "color" ,)
    val color: Int? = 0
)
