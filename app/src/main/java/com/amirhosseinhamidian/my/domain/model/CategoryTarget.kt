package com.amirhosseinhamidian.my.domain.model

import java.io.Serializable

data class CategoryTarget(
    var id: Long? = null,
    val category: Category,
    val hourTarget: Int,
    val minuteTarget: Int,
    val totalTimeTargetInSec:Int? = null,
    val startDateTarget: String,
    val endDateTarget: String,
    val weekNumber: Int,
    val createAt: Long? = null,
    var weeklyGrade:Int? = null
): Serializable
