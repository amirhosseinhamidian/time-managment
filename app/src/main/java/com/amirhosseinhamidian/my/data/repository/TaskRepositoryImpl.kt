package com.amirhosseinhamidian.my.data.repository


import com.example.myapplication.data.db.DB
import com.amirhosseinhamidian.my.data.mapper.toTask
import com.amirhosseinhamidian.my.data.mapper.toTaskEntity
import com.amirhosseinhamidian.my.domain.model.Task
import com.amirhosseinhamidian.my.domain.repository.TaskRepository
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
}