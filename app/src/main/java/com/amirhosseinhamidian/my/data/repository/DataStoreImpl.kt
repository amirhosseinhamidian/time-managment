package com.amirhosseinhamidian.my.data.repository

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.*
import androidx.datastore.preferences.preferencesDataStore
import com.amirhosseinhamidian.my.domain.repository.MyDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

const val DataStore_NAME = "DATASTORE"

val Context.datastore : DataStore<Preferences> by  preferencesDataStore(name = DataStore_NAME)

class DataStoreImpl(private val context: Context): MyDataStore {

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
}