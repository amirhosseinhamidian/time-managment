package com.amirhosseinhamidian.my.domain.repository

import kotlinx.coroutines.flow.Flow

interface MyDataStore {
    suspend fun saveMidnightTime(dateAsKey: String , timeAsValue: Int)
    fun getMidnightTime(dateAsKey: String): Flow<Int>
    suspend fun clearMidnightTime()
    fun isMidnightKeyStored(dateAsKey: String): Flow<Boolean>

    suspend fun saveFreeTimeInWeek(startWeekDate: String, hourFree: Int)
    fun getFreeTimeInWeek(startWeekDate: String): Flow<Int>

    suspend fun saveAmountSleepTimePerDay(sleepTime: Float)
    fun getSleepTimePerDay(): Flow<Float>
}