package com.amirhosseinhamidian.my.presenter.timeRunScreen

import androidx.lifecycle.*
import com.amirhosseinhamidian.my.domain.model.DailyDetails
import com.amirhosseinhamidian.my.domain.model.Task
import com.amirhosseinhamidian.my.domain.repository.MyDataStore
import com.amirhosseinhamidian.my.domain.repository.TaskRepository
import com.amirhosseinhamidian.my.utils.Constants
import com.amirhosseinhamidian.my.utils.Date
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class TimeRunViewModel @Inject constructor(
    private val taskRepository: TaskRepository,
    private val myDataStore: MyDataStore
): ViewModel() {

    fun isAnyTaskRunning(): LiveData<Boolean> {
        val result = MutableLiveData<Boolean>()
        viewModelScope.launch {
            result.postValue(taskRepository.isRunningAnyTask())
        }
        return result
    }

    fun updateTime(task: Task) {
        viewModelScope.launch {
            taskRepository.updateTask(task)
        }
    }

    fun updateTaskStatus(active: Boolean , id: Long) {
        viewModelScope.launch {
            taskRepository.updateTaskStatus(active , id)
        }
    }

    fun getTaskRunningId(): LiveData<Long> {
        val result = MutableLiveData<Long>()
        viewModelScope.launch {
            result.postValue(taskRepository.getRunningTaskIdIfExists())
        }
        return result
    }

    fun insertDetail(details: DailyDetails) {
        viewModelScope.launch {
            taskRepository.insertDailyDetails(dailyDetails = details)
        }
    }

    fun updateDetail(taskId: Long, time: Int, date: String) {
        viewModelScope.launch {
            taskRepository.updateDailyDetails(taskId, time ,date)
        }
    }

    fun checkDailyDetailIsExist(taskId: Long) : LiveData<Int> {
        val result = MutableLiveData<Int>()
        viewModelScope.launch {
            result.postValue(taskRepository.checkDailyDetailIsExist(taskId,Constants.CURRENT_DATE_STATUS))
        }
        return result
    }

    fun getDailyDetailsById(taskId: Long, fewLastDay: Int): LiveData<List<DailyDetails>> {
        val result = MutableLiveData<List<DailyDetails>>()
        viewModelScope.launch {
            result.postValue(taskRepository.getDailyDetailsById(taskId, fewLastDay))
        }
        return result
    }

    fun getTimeBeforeMidnight(): LiveData<Int> {
        return myDataStore.getMidnightTime(Date.getYesterdayDate()).distinctUntilChanged().asLiveData()
    }

    fun clearDataStore() {
        viewModelScope.launch {
            myDataStore.clearMidnightTime()
        }
    }

    fun isMidnightKeyStored(): LiveData<Boolean> {
        return myDataStore.isMidnightKeyStored(Date.getYesterdayDate()).asLiveData()
    }
}