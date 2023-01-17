package com.amirhosseinhamidian.my.presenter.categoryManager

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.amirhosseinhamidian.my.domain.model.Category
import com.amirhosseinhamidian.my.domain.repository.CategoryRepository
import com.amirhosseinhamidian.my.domain.repository.TaskRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CategoryManagerViewModel @Inject constructor(
    private val categoryRepository: CategoryRepository,
    private val taskRepository: TaskRepository
): ViewModel() {

    fun getCategoriesWithNumberTasks(): LiveData<List<Category>> {
        val result = MutableLiveData<List<Category>>()
        viewModelScope.launch {
            val categories = categoryRepository.getAllCategory()
            categories.forEach {
                it.numberTasks = taskRepository.getNumberTasksInCategory(it.name)
            }
            result.postValue(categories)
        }
        return result
    }

    fun updateCategory(category: Category) {
        viewModelScope.launch {
            categoryRepository.updateCategory(category)
        }
    }
}