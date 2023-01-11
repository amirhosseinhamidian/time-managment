package com.amirhosseinhamidian.my.domain.repository

import com.amirhosseinhamidian.my.domain.model.MidnightTime
import kotlinx.coroutines.flow.Flow

interface MyDataStore {
    suspend fun saveMidnightTime(dateAsKey: String , timeAsValue: Int)
    fun getMidnightTime(dateAsKey: String): Flow<Int>
    suspend fun clearMidnightTime()
    fun isMidnightKeyStored(dateAsKey: String): Flow<Boolean>
}