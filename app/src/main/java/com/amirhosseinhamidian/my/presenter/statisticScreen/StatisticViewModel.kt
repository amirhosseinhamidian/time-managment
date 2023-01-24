package com.amirhosseinhamidian.my.presenter.statisticScreen

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.amirhosseinhamidian.my.domain.model.Category
import com.amirhosseinhamidian.my.domain.model.CategoryTarget
import com.amirhosseinhamidian.my.domain.model.DailyDetails
import com.amirhosseinhamidian.my.domain.model.TaskStatisticWeekly
import com.amirhosseinhamidian.my.domain.repository.CategoryRepository
import com.amirhosseinhamidian.my.domain.repository.CategoryTargetRepository
import com.amirhosseinhamidian.my.domain.repository.TaskRepository
import com.amirhosseinhamidian.my.utils.Constants
import com.amirhosseinhamidian.my.utils.DayOfWeek
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject
import kotlin.collections.ArrayList

@HiltViewModel
class StatisticViewModel @Inject constructor(
    private val categoryTargetRepository: CategoryTargetRepository,
    private val taskRepository: TaskRepository,
    private val categoryRepository: CategoryRepository
): ViewModel() {
    private val calendar = Calendar.getInstance()

    fun getNumberWeek(week: Int): Int {
        var numberWeek = 1
        calendar.firstDayOfWeek = Calendar.SATURDAY

        when(week) {
            Constants.CURRENT_WEEK -> {
                numberWeek = calendar.get(Calendar.WEEK_OF_YEAR)
            }
            Constants.NEXT_WEEK -> {
                numberWeek = calendar.get(Calendar.WEEK_OF_YEAR) + 1
            }
            Constants.LAST_WEEK -> {
                numberWeek = calendar.get(Calendar.WEEK_OF_YEAR) - 1
            }
            Constants.LAST_TWO_WEEK -> {
                numberWeek = calendar.get(Calendar.WEEK_OF_YEAR) - 2
            }
            Constants.LAST_THREE_WEEK -> {
                numberWeek = calendar.get(Calendar.WEEK_OF_YEAR) - 3
            }
        }
        if(numberWeek <= 0) {
            numberWeek += 52
        }

        return numberWeek
    }

    fun getStartDateWeek(week: Int): String {
        calendar.set(Calendar.WEEK_OF_YEAR, getNumberWeek(week))
        calendar.set(Calendar.DAY_OF_WEEK, 0)
        val df: DateFormat = SimpleDateFormat("dd MMM", Locale.getDefault())
        val result = df.format(calendar.time)
        calendar.time = Date()
        return result
    }

    fun getEndDateWeek(week: Int): String {
        calendar.set(Calendar.WEEK_OF_YEAR, getNumberWeek(week))
        calendar.set(Calendar.DAY_OF_WEEK, 6)
        val df: DateFormat = SimpleDateFormat("dd MMM", Locale.getDefault())
        val result = df.format(calendar.time)
        calendar.time = Date()
        return result
    }

    fun getDetailsWeekly(week: Int): LiveData<List<DailyDetails>> {
        val result = MutableLiveData<List<DailyDetails>>()
        viewModelScope.launch {
            result.postValue(taskRepository.getWeeklyDetail(getNumberWeekOfYear(week)))
        }
        return result
    }

    fun getNumberWeekOfYear(week: Int): String {
        return "${getNumberWeek(week)} , ${calendar.get(Calendar.YEAR)}"
    }

    fun getCategoryList(): LiveData<List<Category>> {
        val result = MutableLiveData<List<Category>>()
        viewModelScope.launch {
            val list = categoryRepository.getAllCategory()
            result.postValue(list)
        }
        return result
    }

    fun getTargetWeekly(weekStatus: Int): LiveData<List<CategoryTarget>> {
        val result = MutableLiveData<List<CategoryTarget>>()
        viewModelScope.launch {
            val categoryTargetList = categoryTargetRepository.getWeekCategoryTarget(getStartDateWeek(weekStatus))
            result.postValue(categoryTargetList)
        }
        return result
    }

    fun getCategoryGradeWeekly(categoryTargetList: List<CategoryTarget>, weekStatus: Int): LiveData<Double> {
        val result = MutableLiveData<Double>()
        var grade = 0.0
        viewModelScope.launch {
            val dailyDetailsList = taskRepository.getWeeklyDetail(getNumberWeekOfYear(weekStatus))

            for(categoryTarget in categoryTargetList) {
                var totalWeeklyTimeInSec = 0
                dailyDetailsList.forEach { dailyDetails ->
                    if (dailyDetails.categoryName == categoryTarget.category.name) {
                        totalWeeklyTimeInSec += dailyDetails.time
                    }
                }
                val categoryTargetTimeInSec = categoryTarget.hourTarget*3600 + categoryTarget.minuteTarget*60
                grade += if (weekStatus == Constants.CURRENT_WEEK) {
                    val numberDay = DayOfWeek.getDayNumber(calendar.get(Calendar.DAY_OF_WEEK), Constants.FIRST_DAY_SATURDAY)
                    (totalWeeklyTimeInSec / numberDay).toDouble() / (categoryTargetTimeInSec / 7).toDouble()

                } else {
                    (totalWeeklyTimeInSec / categoryTargetTimeInSec).toDouble()
                }
            }
            grade *= 100
            result.postValue(grade)
        }
        return result
    }

    fun getTotalTargetTime(categoryTargetList: List<CategoryTarget> ): Int {
        var totalTime = 0
        categoryTargetList.forEach {
            totalTime += it.totalTimeTargetInSec!!
        }
        return totalTime
    }

    fun getTaskStatisticWeekly(week:Int): LiveData<List<TaskStatisticWeekly>> {
        val result = MutableLiveData<List<TaskStatisticWeekly>>()
        viewModelScope.launch {
            val dailyDetailsListOfWeek = taskRepository.getWeeklyDetail(getNumberWeekOfYear(week)).sortedBy { it.taskId }
            val taskStatisticWeeklyList = arrayListOf<TaskStatisticWeekly>()
            var taskIdCheck = 0L
            dailyDetailsListOfWeek.forEach {
                if (taskIdCheck != it.taskId) {
                    taskIdCheck = it.taskId
                    val task = taskRepository.getTaskById(taskIdCheck)
                    val category = categoryRepository.getCategoryByName(task.category)
                    val totalTaskTimeWeekly = taskRepository.getTotalTaskTimeWeekly(getNumberWeekOfYear(week),taskIdCheck)
                    val taskStatisticWeekly = TaskStatisticWeekly(
                        taskId = taskIdCheck,
                        taskTitle = task.title,
                        category = category,
                        totalTimeWeekly = totalTaskTimeWeekly,
                        dailyDetailsList = getDetailsTaskWeekly(dailyDetailsListOfWeek,taskIdCheck)
                    )
                    taskStatisticWeeklyList.add(taskStatisticWeekly)
                }
            }
            result.postValue(taskStatisticWeeklyList)
        }
        return result
    }

    private fun getDetailsTaskWeekly(
        dailyDetailsListOfWeek: List<DailyDetails>,
        taskIdCheck: Long
    ): ArrayList<DailyDetails> {
        val result = arrayListOf<DailyDetails>()
        dailyDetailsListOfWeek.forEach {
            if (taskIdCheck == it.taskId) {
                result.add(it)
            }
        }
        return result
    }
}