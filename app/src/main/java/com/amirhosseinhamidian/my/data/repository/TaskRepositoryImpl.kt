package com.amirhosseinhamidian.my.data.repository


import android.annotation.SuppressLint
import android.util.Log
import com.amirhosseinhamidian.my.data.db.DB
import com.amirhosseinhamidian.my.data.mapper.toDailyDetails
import com.amirhosseinhamidian.my.data.mapper.toDailyDetailsEntity
import com.amirhosseinhamidian.my.data.mapper.toTask
import com.amirhosseinhamidian.my.data.mapper.toTaskEntity
import com.amirhosseinhamidian.my.domain.model.DailyDetails
import com.amirhosseinhamidian.my.domain.model.Task
import com.amirhosseinhamidian.my.domain.repository.TaskRepository
import com.amirhosseinhamidian.my.utils.Constants
import com.amirhosseinhamidian.my.utils.Date
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject

class TaskRepositoryImpl @Inject  constructor(
    db: DB
): TaskRepository {
    private val dao = db.taskDao
    override suspend fun insertTask(task: Task) {
        return dao.insert(task.toTaskEntity())
    }

    override suspend fun deleteTask(task: Task) {
        return dao.delete(task.toTaskEntity())
    }

    override suspend fun updateTask(task: Task) {
        return dao.update(task.toTaskEntity())
    }

    override suspend fun getAllTsk(): List<Task> {
        return dao.getAllTask().map { it.toTask() }
    }

    override suspend fun getTaskById(id: Long): Task {
        return dao.getTaskById(id).toTask()
    }

    override suspend fun isRunningAnyTask(): Boolean {
        return dao.isRunningAnyTask(Constants.STATUS_RUNNING)
    }

    override suspend fun updateTaskStatus(active: Boolean , id: Long) {
        return if (active) {
            dao.updateTaskStatus(Constants.STATUS_RUNNING, id)
        } else {
            dao.updateTaskStatus(Constants.STATUS_STOPPED , id)
        }
    }

    override suspend fun getRunningTaskIdIfExists(): Long {
        return dao.getRunningTaskIdIfExists(Constants.STATUS_RUNNING)
    }

    override suspend fun insertDailyDetails(dailyDetails: DailyDetails) {
        return dao.insertDailyDetails(dailyDetails.toDailyDetailsEntity())
    }

    override suspend fun updateDailyDetails(taskId: Long, time: Int, date: String) {
        return dao.updateDailyDetails(taskId, time, date)
    }

    override suspend fun checkDailyDetailIsExist(taskId: Long, dayStatus: Int): Int {
        return when (dayStatus) {
            Constants.CURRENT_DATE_STATUS -> {
                dao.checkDailyDetailIsExist(Date.getCurrentDate(), taskId)
            }
            Constants.YESTERDAY_DATE_STATUS -> {
                dao.checkDailyDetailIsExist(Date.getYesterdayDate(), taskId)
            }
            else -> {
                dao.checkDailyDetailIsExist(Date.getCurrentDate(), taskId)
            }
        }
    }

    override suspend fun isDailyDateIsExist(taskId: Long, date: String): Boolean {
        return dao.isDailyDateIsExist(date, taskId)
    }

    override suspend fun getDailyDetailsById(taskId: Long, fewLastDay: Int): List<DailyDetails> {
        return dao.getDailyDetailsById(taskId, fewLastDay).map { it.toDailyDetails() }
    }

    override suspend fun getTodayElapsedTime(): Int {
        val list = dao.getTodayElapsedTime(Date.getCurrentDate())
        var time = 0
        list.forEach {
            time += it.time
        }
        return time
    }

    override suspend fun saveTimeInMidnight(dailyDetails: DailyDetails) {
        if (isDailyDateIsExist(dailyDetails.taskId, Date.getYesterdayDate())) {
            dao.updateDailyDetails(
                taskId = dailyDetails.taskId,
                time = dailyDetails.time + getDayTaskTime(Date.getYesterdayDate(),dailyDetails.taskId),
                date = Date.getYesterdayDate())
        } else {
            dao.insertDailyDetails(dailyDetails.toDailyDetailsEntity())
        }
    }

    override suspend fun getDayTaskTime(date: String, taskId: Long): Int {
        return dao.getDayTaskTime(date,taskId)
    }

    override suspend fun getNumberTasksInCategory(categoryName: String): Int {
        return dao.getNumberTasksInCategory(categoryName)
    }
}