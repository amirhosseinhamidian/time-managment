package com.amirhosseinhamidian.my.data.db

import androidx.room.TypeConverter
import com.amirhosseinhamidian.my.data.db.entity.CategoryEntity
import com.google.gson.Gson

import com.google.gson.reflect.TypeToken

class Converters {
    @TypeConverter
    fun categoryToString(categoryEntity: CategoryEntity): String {
        val gson = Gson()
        return gson.toJson(categoryEntity)
    }

    @TypeConverter
    fun stringToCategory(value: String): CategoryEntity {
        val type = object : TypeToken<CategoryEntity>() {}.type
        return Gson().fromJson(value,type)
    }
}