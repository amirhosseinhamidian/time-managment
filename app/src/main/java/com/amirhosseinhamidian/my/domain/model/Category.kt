package com.amirhosseinhamidian.my.domain.model

data class Category(
    val id: Long? = null,
    val name: String,
    var isSelected: Boolean = false
)
