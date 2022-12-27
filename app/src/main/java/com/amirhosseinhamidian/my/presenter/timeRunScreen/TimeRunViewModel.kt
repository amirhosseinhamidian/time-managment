package com.amirhosseinhamidian.my.presenter.timeRunScreen

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

    fun updateTime(task: Task) {
        viewModelScope.launch {
            taskRepository.updateTask(task)
        }
    }
}