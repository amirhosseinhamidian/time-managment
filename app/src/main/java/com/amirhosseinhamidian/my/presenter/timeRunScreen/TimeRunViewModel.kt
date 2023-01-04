package com.amirhosseinhamidian.my.presenter.timeRunScreen

import android.annotation.SuppressLint
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.amirhosseinhamidian.my.domain.model.DailyDetails
import com.amirhosseinhamidian.my.domain.model.Task
import com.amirhosseinhamidian.my.domain.repository.TaskRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject

@HiltViewModel
class TimeRunViewModel @Inject constructor(
    private val taskRepository: TaskRepository
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

    fun updateDetail(taskId: Long, time: Int) {
        viewModelScope.launch {
            taskRepository.updateDailyDetails(taskId, time)
        }
    }

    fun checkDailyDetailIsExist(date: String , taskId: Long) : LiveData<Int> {
        val result = MutableLiveData<Int>()
        viewModelScope.launch {
            result.postValue(taskRepository.checkDailyDetailIsExist(date, taskId))
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

    @SuppressLint("SimpleDateFormat")
    fun getCurrentDate(): String {
        val calendar: Calendar = Calendar.getInstance()
        val sdf = SimpleDateFormat("yyyy-MM-dd")
        return sdf.format(calendar.time)
    }
}