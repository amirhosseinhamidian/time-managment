package com.amirhosseinhamidian.my.data.db.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy.REPLACE
import androidx.room.Query
import androidx.room.Update
import com.amirhosseinhamidian.my.data.db.entity.CategoryTargetEntity

@Dao
interface CategoryTargetDao {

    @Insert(onConflict = REPLACE)
    suspend fun insert(categoryTargetEntity: CategoryTargetEntity): Long

    @Update
    suspend fun update(categoryTargetEntity: CategoryTargetEntity): Int

    @Delete
    suspend fun delete(categoryTargetEntity: CategoryTargetEntity)

    @Query("SELECT * FROM category_target_table WHERE startDateTarget=:startDayWeek")
    suspend fun getWeekCategoryTarget(startDayWeek: String): List<CategoryTargetEntity>
}