package com.amirhosseinhamidian.my.presenter.homeScreen

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
class HomeViewModel@Inject constructor(
    private val taskRepository: TaskRepository,
): ViewModel() {

    fun getAllTask(): LiveData<List<Task>> {
        val result = MutableLiveData<List<Task>>()
        viewModelScope.launch {
            val list = taskRepository.getAllTsk()
            result.postValue(list)
        }
        return result
    }

    fun deleteTask(taskToClear: Task) {
        viewModelScope.launch {
            taskRepository.deleteTask(taskToClear)
        }
    }

    fun getTodayElapsedTime(): LiveData<Int> {
        val result = MutableLiveData<Int>()
        viewModelScope.launch {
           result.postValue(taskRepository.getTodayElapsedTime())
        }
        return result
    }
}