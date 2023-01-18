package com.amirhosseinhamidian.my.presenter.addTargetScreed

import android.graphics.Color
import androidx.lifecycle.*
import com.amirhosseinhamidian.my.domain.model.Category
import com.amirhosseinhamidian.my.domain.model.CategoryTarget
import com.amirhosseinhamidian.my.domain.model.ChartValue
import com.amirhosseinhamidian.my.domain.repository.CategoryRepository
import com.amirhosseinhamidian.my.domain.repository.CategoryTargetRepository
import com.amirhosseinhamidian.my.domain.repository.MyDataStore
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
class AddTargetViewModel @Inject constructor(
    private val categoryRepository: CategoryRepository,
    private val categoryTargetRepository: CategoryTargetRepository,
    private val dataStore: MyDataStore
) : ViewModel() {
    private val calendar = Calendar.getInstance()


    fun getCategoryList(): LiveData<List<Category>> {
        val result = MutableLiveData<List<Category>>()
        viewModelScope.launch {
            val list = categoryRepository.getAllCategory()
            result.postValue(list)
        }
        return result
    }

    fun isThereCategory(name: String): LiveData<Boolean> {
        val result = MutableLiveData<Boolean>()
        viewModelScope.launch {
            result.postValue(categoryRepository.isThereCategory(name))
        }
        return result
    }

    fun addNewCategory(categoryName: String, color: Int): LiveData<Long> {
        val result = MutableLiveData<Long>()
        viewModelScope.launch {
            result.postValue(
                categoryRepository.insertCategory(
                    Category(
                        name = categoryName,
                        color = color
                    )
                )
            )
        }
        return result
    }

    fun saveCategoryTarget(categoryTarget: CategoryTarget): LiveData<Long> {
        val result = MutableLiveData<Long>()
        viewModelScope.launch {
            result.postValue(categoryTargetRepository.insertCategoryTarget(categoryTarget))
        }
        return result
    }

    fun getNumberWeek(week: Int): Int {
        var numberWeek = 0
        calendar.firstDayOfWeek = Calendar.SATURDAY
        if (week == Constants.CURRENT_WEEK) {
            numberWeek = calendar.get(Calendar.WEEK_OF_YEAR)
        }

        if (week == Constants.NEXT_WEEK) {
            numberWeek = calendar.get(Calendar.WEEK_OF_YEAR) + 1
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

    fun getTargetsWeek(startDayWeek: String): LiveData<List<CategoryTarget>> {
        val result = MutableLiveData<List<CategoryTarget>>()
        viewModelScope.launch {
            result.postValue(categoryTargetRepository.getWeekCategoryTarget(startDayWeek))
        }
        return result
    }

    fun getTotalTimeLeftInWeek(sleepTime: Float ,week : Int): Int {
        var totalWeekUpTimeInSec = 0f
        if (week == Constants.CURRENT_WEEK) {
            val cal = Calendar.getInstance()
            cal.firstDayOfWeek = Calendar.SATURDAY
            val currentDay = DayOfWeek.getDayNumber(cal.get(Calendar.DAY_OF_WEEK),Constants.FIRST_DAY_SATURDAY)
            val daysPast = currentDay - DayOfWeek.getDayNumber(cal.firstDayOfWeek,Constants.FIRST_DAY_SATURDAY)
            val daysNext = 7 - daysPast
            totalWeekUpTimeInSec = ((168 - daysPast*24 - sleepTime*daysNext) * 3600)
        }else if (week == Constants.NEXT_WEEK) {
            totalWeekUpTimeInSec = ((168 - sleepTime*7) * 3600)
        }
        return totalWeekUpTimeInSec.toInt()
    }

    fun getChartValues(startDayWeek: String, totalWeekUpTimeInSec: Int): LiveData<List<ChartValue>> {
        val result = MutableLiveData<List<ChartValue>>()
        var timeWeekUp = totalWeekUpTimeInSec
        val timeList = arrayListOf<ChartValue>()
        viewModelScope.launch {
            val list = categoryTargetRepository.getWeekCategoryTarget(startDayWeek)
            list.forEach {
                timeWeekUp -= it.totalTimeTargetInSec!!
                timeList.add(ChartValue(
                    value = it.totalTimeTargetInSec.toFloat(),
                    color = it.category.color!!
                ))
            }
            timeList.add(0, ChartValue(
                value = timeWeekUp.toFloat(),
                color = Color.parseColor("#525050"))
            )
            result.postValue(timeList)
        }
        return result
    }

    fun saveAmountSleepTimePerDay(sleepTime: Float) {
        viewModelScope.launch {
            dataStore.saveAmountSleepTimePerDay(sleepTime)
        }
    }

    fun getSleepTimePerDay(): LiveData<Float> {
        return dataStore.getSleepTimePerDay().asLiveData()
    }

    fun deleteCategoryTarget(categoryTarget: CategoryTarget) {
        viewModelScope.launch {
            categoryTargetRepository.deleteCategoryTarget(categoryTarget)
        }
    }

    fun updateCategoryTarget(updateCategoryTarget: CategoryTarget): LiveData<Int>{
        val result = MutableLiveData<Int>()
        viewModelScope.launch {
           result.postValue(categoryTargetRepository.updateCategoryTarget(updateCategoryTarget))
        }
        return result
    }
}