package com.amirhosseinhamidian.my.presenter.timeRunScreen

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.amirhosseinhamidian.my.domain.model.Task
import com.amirhosseinhamidian.my.domain.repository.TaskRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
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
}