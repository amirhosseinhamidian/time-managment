package com.amirhosseinhamidian.my.di


import com.amirhosseinhamidian.my.data.repository.CategoryRepositoryImpl
import com.amirhosseinhamidian.my.data.repository.CategoryTargetRepositoryImpl
import com.amirhosseinhamidian.my.data.repository.DataStoreImpl
import com.amirhosseinhamidian.my.data.repository.TaskRepositoryImpl
import com.amirhosseinhamidian.my.domain.repository.CategoryRepository
import com.amirhosseinhamidian.my.domain.repository.CategoryTargetRepository
import com.amirhosseinhamidian.my.domain.repository.MyDataStore
import com.amirhosseinhamidian.my.domain.repository.TaskRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds
    @Singleton
    abstract fun bindTaskRepository(taskRepositoryImpl: TaskRepositoryImpl): TaskRepository

    @Binds
    @Singleton
    abstract fun bindCategoryRepository(categoryRepositoryImpl: CategoryRepositoryImpl): CategoryRepository

    @Binds
    @Singleton
    abstract fun bindDataStore(dataStoreImpl: DataStoreImpl): MyDataStore

    @Binds
    @Singleton
    abstract fun bindCategoryTargetRepository(categoryTargetRepositoryImpl: CategoryTargetRepositoryImpl): CategoryTargetRepository
}