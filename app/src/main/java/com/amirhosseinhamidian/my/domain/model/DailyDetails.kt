package com.amirhosseinhamidian.my.domain.model

data class DailyDetails(
    val id: Long? = null,
    val taskId: Long,
    val date: String,
    val time: Int
)