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

    fun saveFreeTimeInWeek(startWeekDate: String, hourFree: Int) {
        viewModelScope.launch {
            dataStore.saveFreeTimeInWeek(startWeekDate, hourFree)
        }
    }

    fun getFreeTimeInWeek(startWeekDate: String): LiveData<Int> {
        return dataStore.getFreeTimeInWeek(startWeekDate).asLiveData()
    }

    fun getChartValues(startDayWeek: String, freeTimeInWeek: Int): LiveData<List<ChartValue>> {
        val result = MutableLiveData<List<ChartValue>>()
        var freeTimeInSec = freeTimeInWeek * 3600
        val timeList = arrayListOf<ChartValue>()
        viewModelScope.launch {
            val list = categoryTargetRepository.getWeekCategoryTarget(startDayWeek)
            list.forEach {
                freeTimeInSec -= it.totalTimeTargetInSec!!
                timeList.add(ChartValue(
                    value = it.totalTimeTargetInSec.toFloat(),
                    color = it.category.color!!
                ))
            }
            timeList.add(0, ChartValue(
                value = freeTimeInSec.toFloat(),
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

}