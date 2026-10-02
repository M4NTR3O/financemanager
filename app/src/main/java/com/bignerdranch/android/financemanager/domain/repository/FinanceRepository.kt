package com.bignerdranch.android.financemanager.domain.repository

import com.bignerdranch.android.financemanager.domain.model.Category
import com.bignerdranch.android.financemanager.domain.model.CategoryType
import com.bignerdranch.android.financemanager.domain.model.Transaction
import com.bignerdranch.android.financemanager.domain.model.User
import kotlinx.coroutines.flow.Flow
import java.math.BigDecimal

sealed interface CategoryDeleteResult {
    data object Ok : CategoryDeleteResult
    data object SystemCategory : CategoryDeleteResult
    data object HasTransactions : CategoryDeleteResult
}

interface FinanceRepository {
    // User
    fun observeUser(): Flow<User?>
    suspend fun getUser(): User?
    suspend fun createUser(initialBalance: BigDecimal): Long

    // Category
    fun observeCategories(): Flow<List<Category>>
    fun observeCategories(type: CategoryType): Flow<List<Category>>
    suspend fun addCategory(name: String, parentId: Long?, type: CategoryType): Long
    suspend fun deleteCategory(id: Long): CategoryDeleteResult

    // Transaction
    fun observeTransactions(): Flow<List<Transaction>>
    fun observeTransactions(from: java.time.Instant, to: java.time.Instant): Flow<List<Transaction>>
    suspend fun addTransaction(t: Transaction): Long
    suspend fun updateTransaction(t: Transaction)
    suspend fun deleteTransaction(t: Transaction)
    suspend fun getTransaction(id: Long): Transaction?
}