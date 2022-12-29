package com.amirhosseinhamidian.my.di

import android.content.Context
import androidx.room.Room
import com.amirhosseinhamidian.my.data.db.DB
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {
    @Singleton
    @Provides
    fun provideDatabase(@ApplicationContext context: Context): DB {
        return Room.databaseBuilder(
            context,
            DB::class.java,
            "Taskdb.db"
        ).build()
    }
}