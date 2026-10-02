package com.bignerdranch.android.financemanager.data.repository

import com.bignerdranch.android.financemanager.data.db.AppDatabase
import com.bignerdranch.android.financemanager.data.db.entity.CategoryEntity
import com.bignerdranch.android.financemanager.data.db.entity.TransactionEntity
import com.bignerdranch.android.financemanager.data.db.entity.UserEntity
import com.bignerdranch.android.financemanager.domain.model.*
import com.bignerdranch.android.financemanager.domain.repository.CategoryDeleteResult
import com.bignerdranch.android.financemanager.domain.repository.FinanceRepository
import com.bignerdranch.android.financemanager.domain.util.toBigDecimalMoney
import com.bignerdranch.android.financemanager.domain.util.toMinorUnits
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.math.BigDecimal
import java.time.Instant
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FinanceRepositoryImpl @Inject constructor(
    private val db: AppDatabase
) : FinanceRepository {

    private val userDao = db.userDao
    private val categoryDao = db.categoryDao
    private val txDao = db.transactionDao

    // ---------- Mappers ----------
    private fun UserEntity.toDomain() = User(
        id = id, createdAt = Instant.ofEpochMilli(createdAt),
        initialBalance = initialBalanceMinor.toBigDecimalMoney()
    )
    private fun CategoryEntity.toDomain() = Category(
        id = id, name = name, parentCategoryId = parentCategoryId,
        type = type, isSystem = isSystem
    )
    private fun TransactionEntity.toDomain() = Transaction(
        id = id, userId = userId, categoryId = categoryId,
        amount = amountMinor.toBigDecimalMoney(),
        dateTime = Instant.ofEpochMilli(dateTime),
        comment = comment
    )

    // ---------- User ----------
    override fun observeUser(): Flow<User?> = userDao.observeFirst().map { it?.toDomain() }
    override suspend fun getUser(): User? = userDao.getFirst()?.toDomain()
    override suspend fun createUser(initialBalance: BigDecimal): Long =
        userDao.insert(UserEntity(createdAt = System.currentTimeMillis(),
            initialBalanceMinor = initialBalance.toMinorUnits()))

    // ---------- Category ----------
    override fun observeCategories(): Flow<List<Category>> =
        categoryDao.observeAll().map { list -> list.map { it.toDomain() } }

    override fun observeCategories(type: CategoryType): Flow<List<Category>> =
        categoryDao.observeByType(type).map { list -> list.map { it.toDomain() } }

    override suspend fun addCategory(name: String, parentId: Long?, type: CategoryType): Long =
        categoryDao.insert(CategoryEntity(
            name = name, parentCategoryId = parentId, type = type, isSystem = false))

    override suspend fun deleteCategory(id: Long): CategoryDeleteResult {
        val cat = categoryDao.getById(id) ?: return CategoryDeleteResult.Ok
        if (cat.isSystem) return CategoryDeleteResult.SystemCategory
        if (categoryDao.transactionCount(id) > 0) return CategoryDeleteResult.HasTransactions
        // Дети становятся корневыми (SET_NULL не сработает при удалении самого родителя)
        categoryDao.detachChildren(id)
        categoryDao.delete(cat)
        return CategoryDeleteResult.Ok
    }

    // ---------- Transaction ----------
    override fun observeTransactions(): Flow<List<Transaction>> =
        txDao.observeAll().map { list -> list.map { it.toDomain() } }

    override fun observeTransactions(from: Instant, to: Instant): Flow<List<Transaction>> =
        txDao.observeBetween(from.toEpochMilli(), to.toEpochMilli())
            .map { list -> list.map { it.toDomain() } }

    override suspend fun addTransaction(t: Transaction): Long =
        txDao.insert(TransactionEntity(
            userId = t.userId, categoryId = t.categoryId,
            amountMinor = t.amount.toMinorUnits(),
            dateTime = t.dateTime.toEpochMilli(), comment = t.comment))

    override suspend fun updateTransaction(t: Transaction) =
        txDao.update(TransactionEntity(
            id = t.id, userId = t.userId, categoryId = t.categoryId,
            amountMinor = t.amount.toMinorUnits(),
            dateTime = t.dateTime.toEpochMilli(), comment = t.comment))

    override suspend fun deleteTransaction(t: Transaction) {
        val e = txDao.getById(t.id) ?: return
        txDao.delete(e)
    }

    override suspend fun getTransaction(id: Long): Transaction? = txDao.getById(id)?.toDomain()
}