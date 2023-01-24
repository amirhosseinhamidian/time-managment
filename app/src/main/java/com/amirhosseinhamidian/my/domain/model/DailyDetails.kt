package com.amirhosseinhamidian.my.domain.model

data class DailyDetails(
    val id: Long? = null,
    val taskId: Long,
    val categoryName: String? = null,
    val weekNumberOfYear: String? = null,
    val date: String,
    val time: Int,
)