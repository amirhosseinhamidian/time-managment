package com.amirhosseinhamidian.my.data.db


import androidx.room.AutoMigration
import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.amirhosseinhamidian.my.data.db.dao.CategoryDao
import com.amirhosseinhamidian.my.data.db.dao.CategoryTargetDao
import com.amirhosseinhamidian.my.data.db.dao.TaskDao
import com.amirhosseinhamidian.my.data.db.entity.CategoryEntity
import com.amirhosseinhamidian.my.data.db.entity.CategoryTargetEntity
import com.amirhosseinhamidian.my.data.db.entity.DailyDetailsEntity
import com.amirhosseinhamidian.my.data.db.entity.TaskEntity

@Database(entities = [
    TaskEntity::class,
    CategoryEntity::class,
    DailyDetailsEntity::class,
    CategoryTargetEntity::class],
    version = 7,
    autoMigrations = [
        AutoMigration(from = 1, to = 2),
        AutoMigration(from = 2 , to = 3),
        AutoMigration(from = 3 , to = 4),
        AutoMigration(from = 4 , to = 5),
        AutoMigration(from = 5 , to = 6),
        AutoMigration(from = 6 , to = 7),
    ]
)
@TypeConverters(Converters::class)
abstract class DB: RoomDatabase() {
    abstract val taskDao: TaskDao
    abstract val categoryDao: CategoryDao
    abstract val categoryTargetDao: CategoryTargetDao
}
