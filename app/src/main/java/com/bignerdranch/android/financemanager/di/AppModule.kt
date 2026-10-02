package com.bignerdranch.android.financemanager.di

import android.content.Context
import com.bignerdranch.android.financemanager.data.db.AppDatabase
import com.bignerdranch.android.financemanager.data.repository.FinanceRepositoryImpl
import com.bignerdranch.android.financemanager.domain.repository.FinanceRepository
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {
    @Provides @Singleton
    fun provideDb(@ApplicationContext ctx: Context): AppDatabase = AppDatabase.get(ctx)
}

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {
    @Binds @Singleton
    abstract fun bindRepo(impl: FinanceRepositoryImpl): FinanceRepository
}