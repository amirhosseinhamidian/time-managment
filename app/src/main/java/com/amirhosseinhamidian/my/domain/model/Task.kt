package com.amirhosseinhamidian.my.domain.model

import java.io.Serializable


data class Task(
    val id: Long? = null,
    val title: String,
    val description: String? = null,
    var taskStatus: Int? = null,
    val category: String,
    var elapsedTime: Int
) : Serializable {
    companion object {
        fun hiddenTask() : Task {
            return Task(-1,"", category = "", elapsedTime = 0)
        }
    }
}