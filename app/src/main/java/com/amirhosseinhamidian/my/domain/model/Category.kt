package com.amirhosseinhamidian.my.domain.model

data class Category(
    val id: Long? = null,
    val name: String,
    val color: Int? = 0,
    var isSelected: Boolean = false,
    var numberTasks: Int = 0
) {
    companion object {
        fun getAllCategoryItem() : Category {
            return Category(-1, name = "All")
        }
    }
}
