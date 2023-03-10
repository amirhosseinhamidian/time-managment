package com.amirhosseinhamidian.my.data.db.dao


import androidx.room.*
import androidx.room.OnConflictStrategy.REPLACE
import com.amirhosseinhamidian.my.data.db.entity.DailyDetailsEntity
import com.amirhosseinhamidian.my.data.db.entity.TaskEntity
import com.amirhosseinhamidian.my.domain.model.DailyDetails

@Dao
interface TaskDao {
    //region tasks
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

    @Query("SELECT COUNT(id) FROM task_table WHERE category=:categoryName")
    suspend fun getNumberTasksInCategory(categoryName: String): Int

    //endregion

    //region daily details
    @Insert(onConflict = REPLACE)
    suspend fun insertDailyDetails(dailyDetailsEntity: DailyDetailsEntity)

    @Query("UPDATE daily_details_table SET time=:time WHERE taskId=:taskId AND date=:date")
    suspend fun updateDailyDetails(taskId: Long, time: Int, date: String)

    @Query("SELECT * FROM daily_details_table")
    suspend fun getAllDailyDetails(): List<DailyDetailsEntity>

    @Query("SELECT time FROM daily_details_table WHERE date=:date AND taskId=:taskId")
    suspend fun checkDailyDetailIsExist(date: String , taskId: Long): Int

    @Query("SELECT EXISTS (SELECT * FROM daily_details_table WHERE date=:date AND taskId=:taskId)")
    suspend fun isDailyDateIsExist(date: String , taskId: Long): Boolean

    @Query("SELECT * FROM daily_details_table WHERE taskId=:taskId ORDER BY id DESC LIMIT :fewLastDay")
    suspend fun getDailyDetailsById(taskId: Long , fewLastDay: Int): List<DailyDetailsEntity>

    @Query("SELECT * FROM daily_details_table WHERE date=:date")
    suspend fun getTodayElapsedTime(date: String): List<DailyDetailsEntity>

    @Query("SELECT time FROM daily_details_table WHERE date=:date AND taskId=:taskId")
    suspend fun getDayTaskTime(date: String, taskId: Long): Int

    @Query("UPDATE daily_details_table SET categoryName=:categoryName WHERE taskId=:taskId")
    suspend fun updateCategoryNameDailyDetails(categoryName: String, taskId: Long)

    @Query("SELECT * FROM daily_details_table WHERE weekNumberOfYear=:numberOfWeek")
    suspend fun getWeeklyDetail(numberOfWeek: String): List<DailyDetailsEntity>

    @Query("SELECT SUM(time) FROM daily_details_table WHERE weekNumberOfYear=:weekNumberOfYear AND taskId=:taskId")
    suspend fun getTotalTaskTimeWeekly(weekNumberOfYear: String , taskId: Long): Int

    @Query("UPDATE daily_details_table SET weekNumberOfYear=:weekNumberOfYear WHERE id=:id")
    suspend fun updateWeekNumber(weekNumberOfYear: String, id: Long)

    @Query("SELECT * FROM daily_details_table WHERE weekNumberOfYear=:numberOfWeek AND categoryName=:categorySelected")
    suspend fun getWeeklyDetailByCategory(numberOfWeek: String, categorySelected: String): List<DailyDetailsEntity>
    //endregion
}