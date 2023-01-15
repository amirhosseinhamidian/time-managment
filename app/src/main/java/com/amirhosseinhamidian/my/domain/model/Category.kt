package com.amirhosseinhamidian.my.domain.model

data class Category(
    val id: Long? = null,
    val name: String,
    val color: Int? = 0,
    var isSelected: Boolean = false
)
