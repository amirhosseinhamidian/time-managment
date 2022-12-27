package com.amirhosseinhamidian.my.data.db.dao

import androidx.room.*
import com.amirhosseinhamidian.my.data.db.entity.CategoryEntity


@Dao
interface CategoryDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(categoryEntity: CategoryEntity): Long

    @Delete
    suspend fun delete(categoryEntity: CategoryEntity)

    @Query("SELECT * FROM category_table")
    suspend fun getAllCategory(): List<CategoryEntity>

    @Query("SELECT * FROM category_table WHERE id=:id")
    suspend fun getCategoryById(id:Long): CategoryEntity
}