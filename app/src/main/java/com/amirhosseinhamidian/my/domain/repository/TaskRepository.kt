package com.amirhosseinhamidian.my.domain.repository

import com.amirhosseinhamidian.my.domain.model.DailyDetails
import com.amirhosseinhamidian.my.domain.model.Task

interface TaskRepository {
    suspend fun insertTask(task: Task)
    suspend fun deleteTask(task: Task)
    suspend fun updateTask(task: Task)
    suspend fun getAllTsk(): List<Task>
    suspend fun getTaskById(id: Long): Task
    suspend fun isRunningAnyTask(): Boolean
    suspend fun updateTaskStatus(active: Boolean, id: Long)
    suspend fun getRunningTaskIdIfExists(): Long
    suspend fun insertDailyDetails(dailyDetails: DailyDetails)
    suspend fun updateDailyDetails(taskId: Long, time: Int, date: String)
    suspend fun checkDailyDetailIsExist(taskId: Long, dayStatus: Int): Int
    suspend fun isDailyDateIsExist(taskId: Long, date: String): Boolean
    suspend fun getDailyDetailsById(taskId: Long , fewLastDay: Int): List<DailyDetails>
    suspend fun getTodayElapsedTime(): Int
    suspend fun saveTimeInMidnight(dailyDetails: DailyDetails)
    suspend fun getDayTaskTime(date: String, taskId: Long): Int
}