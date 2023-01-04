package com.amirhosseinhamidian.my.data.db.dao


import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy.REPLACE
import androidx.room.Query
import androidx.room.Update
import com.amirhosseinhamidian.my.data.db.entity.DailyDetailsEntity
import com.amirhosseinhamidian.my.data.db.entity.TaskEntity
import com.amirhosseinhamidian.my.domain.model.DailyDetails

@Dao
interface TaskDao {

    @Insert(onConflict = REPLACE)
    suspend fun insert(taskEntity: TaskEntity)

    @Delete
    suspend fun delete(taskEntity: TaskEntity)

    @Update
    suspend fun update(taskEntity: TaskEntity)

    @Query("SELECT * FROM task_table")
    suspend fun getAllTask(): List<TaskEntity>

    @Query("SELECT * FROM task_table WHERE id=:id")
    suspend fun getTaskById(id:Long): TaskEntity

    @Query("SELECT EXISTS (SELECT * FROM task_table WHERE task_status=:status)")
    suspend fun isRunningAnyTask(status: Int): Boolean

    @Query("UPDATE task_table SET task_status=:status WHERE id=:id")
    suspend fun updateTaskStatus(status: Int, id: Long)

    @Query("SELECT id From task_table WHERE task_status=:status")
    suspend fun getRunningTaskIdIfExists(status: Int): Long

    //region daily details
    @Insert(onConflict = REPLACE)
    suspend fun insertDailyDetails(dailyDetailsEntity: DailyDetailsEntity)

    @Query("UPDATE daily_details_table SET time=:time WHERE taskId=:taskId")
    suspend fun updateDailyDetails(taskId: Long, time: Int)

    @Query("SELECT time FROM daily_details_table WHERE date=:date AND taskId=:taskId")
    suspend fun checkDailyDetailIsExist(date: String , taskId: Long): Int

    @Query("SELECT * FROM daily_details_table WHERE taskId=:taskId ORDER BY id DESC LIMIT :fewLastDay")
    suspend fun getDailyDetailsById(taskId: Long , fewLastDay: Int): List<DailyDetailsEntity>
    //endregion
}