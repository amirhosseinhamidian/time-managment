package com.amirhosseinhamidian.my.presenter.addTargetScreed

import androidx.lifecycle.*
import com.amirhosseinhamidian.my.domain.model.Category
import com.amirhosseinhamidian.my.domain.model.CategoryTarget
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

@HiltViewModel
class AddTargetViewModel @Inject constructor(
    private val categoryRepository: CategoryRepository,
    private val categoryTargetRepository: CategoryTargetRepository,
    private val dataStore: MyDataStore
): ViewModel() {
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
            result.postValue(categoryRepository.insertCategory(Category(name = categoryName, color = color)))
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
        calendar.set(Calendar.WEEK_OF_YEAR,getNumberWeek(week))
        calendar.set(Calendar.DAY_OF_WEEK,0)
        val df: DateFormat = SimpleDateFormat("dd MMM", Locale.getDefault())
        val result =  df.format(calendar.time)
        calendar.time = Date()
        return result
    }

    fun getEndDateWeek(week: Int): String {
        calendar.set(Calendar.WEEK_OF_YEAR,getNumberWeek(week))
        calendar.set(Calendar.DAY_OF_WEEK,6)
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
            dataStore.saveFreeTimeInWeek(startWeekDate,hourFree)
        }
    }

    fun getFreeTimeInWeek(startWeekDate: String): LiveData<Int> {
        return dataStore.getFreeTimeInWeek(startWeekDate).asLiveData()
    }

}