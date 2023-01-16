package com.amirhosseinhamidian.my.data.repository

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.*
import androidx.datastore.preferences.preferencesDataStore
import com.amirhosseinhamidian.my.data.repository.DataStoreImpl.PreferencesKeys.SLEEP_TIME_KEY
import com.amirhosseinhamidian.my.domain.repository.MyDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

const val DataStore_NAME = "DATASTORE"

val Context.datastore : DataStore<Preferences> by  preferencesDataStore(name = DataStore_NAME)

class DataStoreImpl(private val context: Context): MyDataStore {

    private object PreferencesKeys {
        val SLEEP_TIME_KEY = floatPreferencesKey("sleep_time_key")
    }

    override suspend fun saveMidnightTime(dateAsKey: String , timeAsValue: Int) {
        val dataStoreKey = intPreferencesKey(dateAsKey)
        context.datastore.edit {
            it[dataStoreKey] = timeAsValue
        }
    }

    override fun getMidnightTime(dateAsKey: String): Flow<Int> = context.datastore.data
        .map {
            it[intPreferencesKey(dateAsKey)]?:0
        }


    override suspend fun clearMidnightTime() {
        context.datastore.edit {
            it.clear()
        }
    }

    override fun isMidnightKeyStored(dateAsKey: String): Flow<Boolean> = context.datastore.data.map {
        it.contains(intPreferencesKey(dateAsKey))
    }

    override suspend fun saveFreeTimeInWeek(startWeekDate: String, hourFree: Int) {
        val dataStoreKey = intPreferencesKey(startWeekDate)
        context.datastore.edit {
            it[dataStoreKey] = hourFree
        }
    }

    override fun getFreeTimeInWeek(startWeekDate: String): Flow<Int> = context.datastore.data
        .map {
            it[intPreferencesKey(startWeekDate)]?:112
        }

    override suspend fun saveAmountSleepTimePerDay(sleepTime: Float) {
        context.datastore.edit {
            it[SLEEP_TIME_KEY] = sleepTime
        }
    }

    override fun getSleepTimePerDay(): Flow<Float> = context.datastore.data
        .map {
            it[SLEEP_TIME_KEY] ?: 8f
        }
}