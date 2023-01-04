package com.amirhosseinhamidian.my.data.db


import androidx.room.AutoMigration
import androidx.room.Database
import androidx.room.RoomDatabase
import com.amirhosseinhamidian.my.data.db.dao.CategoryDao
import com.amirhosseinhamidian.my.data.db.dao.TaskDao
import com.amirhosseinhamidian.my.data.db.entity.CategoryEntity
import com.amirhosseinhamidian.my.data.db.entity.DailyDetailsEntity
import com.amirhosseinhamidian.my.data.db.entity.TaskEntity

@Database(entities = [TaskEntity::class , CategoryEntity::class, DailyDetailsEntity::class],
    version = 4,
    autoMigrations = [
        AutoMigration(from = 1, to = 2),
        AutoMigration(from = 2 , to = 3),
        AutoMigration(from = 3 , to = 4),
    ]
)
abstract class DB: RoomDatabase() {
    abstract val taskDao: TaskDao
    abstract val categoryDao: CategoryDao
}
