package com.example.myapplication.data.db


import androidx.room.Database
import androidx.room.RoomDatabase
import com.amirhosseinhamidian.my.data.db.dao.CategoryDao
import com.amirhosseinhamidian.my.data.db.dao.TaskDao
import com.amirhosseinhamidian.my.data.db.entity.CategoryEntity
import com.amirhosseinhamidian.my.data.db.entity.TaskEntity

@Database(entities = [TaskEntity::class , CategoryEntity::class],
    version = 1)
abstract class DB: RoomDatabase() {
    abstract val taskDao: TaskDao
    abstract val categoryDao: CategoryDao

}
