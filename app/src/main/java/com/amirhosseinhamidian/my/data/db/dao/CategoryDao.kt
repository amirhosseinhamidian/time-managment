package com.amirhosseinhamidian.my.data.db.dao

import androidx.room.*
import com.amirhosseinhamidian.my.data.db.entity.CategoryEntity


@Dao
interface CategoryDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(categoryEntity: CategoryEntity): Long

    @Update
    suspend fun update(categoryEntity: CategoryEntity)

    @Delete
    suspend fun delete(categoryEntity: CategoryEntity)

    @Query("SELECT * FROM category_table ORDER BY id DESC")
    suspend fun getAllCategory(): List<CategoryEntity>

    @Query("SELECT * FROM category_table WHERE id=:id")
    suspend fun getCategoryById(id:Long): CategoryEntity

    @Query("SELECT EXISTS (SELECT * FROM category_table WHERE name=:name)")
    suspend fun isThereCategory(name: String) : Boolean
}