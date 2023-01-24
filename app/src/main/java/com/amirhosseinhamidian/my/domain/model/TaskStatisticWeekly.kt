package com.amirhosseinhamidian.my.domain.model

import java.io.Serializable

data class TaskStatisticWeekly(
    val taskId: Long,
    val taskTitle: String,
    val category: Category,
    val totalTimeWeekly: Int,
    val dailyDetailsList: List<DailyDetails>,

    var isExpanded: Boolean = false
): Serializable
