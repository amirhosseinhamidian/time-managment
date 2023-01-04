package com.amirhosseinhamidian.my.data.repository


import android.annotation.SuppressLint
import com.amirhosseinhamidian.my.data.db.DB
import com.amirhosseinhamidian.my.data.mapper.toDailyDetails
import com.amirhosseinhamidian.my.data.mapper.toDailyDetailsEntity
import com.amirhosseinhamidian.my.data.mapper.toTask
import com.amirhosseinhamidian.my.data.mapper.toTaskEntity
import com.amirhosseinhamidian.my.domain.model.DailyDetails
import com.amirhosseinhamidian.my.domain.model.Task
import com.amirhosseinhamidian.my.domain.repository.TaskRepository
import com.amirhosseinhamidian.my.utils.Constants
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

    override suspend fun updateDailyDetails(taskId: Long, time: Int) {
        return dao.updateDailyDetails(taskId, time)
    }

    override suspend fun checkDailyDetailIsExist(taskId: Long): Int {
        return dao.checkDailyDetailIsExist(getCurrentDate(), taskId)
    }

    override suspend fun getDailyDetailsById(taskId: Long, fewLastDay: Int): List<DailyDetails> {
        return dao.getDailyDetailsById(taskId, fewLastDay).map { it.toDailyDetails() }
    }

    override suspend fun getTodayElapsedTime(): Int {
        val list = dao.getTodayElapsedTime(getCurrentDate())
        var time = 0
        list.forEach {
            time += it.time
        }
        return time
    }

    @SuppressLint("SimpleDateFormat")
    fun getCurrentDate(): String {
        val calendar: Calendar = Calendar.getInstance()
        val sdf = SimpleDateFormat("yyyy-MM-dd")
        return sdf.format(calendar.time)
    }

}