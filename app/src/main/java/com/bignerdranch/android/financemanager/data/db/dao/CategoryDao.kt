package com.bignerdranch.android.financemanager.data.db.dao

import androidx.room.*
import com.bignerdranch.android.financemanager.data.db.entity.CategoryEntity
import com.bignerdranch.android.financemanager.domain.model.CategoryType
import kotlinx.coroutines.flow.Flow

@Dao
interface CategoryDao {
    @Insert suspend fun insert(c: CategoryEntity): Long
    @Insert suspend fun insertAll(items: List<CategoryEntity>)
    @Update suspend fun update(c: CategoryEntity)
    @Delete suspend fun delete(c: CategoryEntity)
    @Query("UPDATE categories SET parentCategoryId = NULL WHERE parentCategoryId = :parentId")
    suspend fun detachChildren(parentId: Long)

    @Query("SELECT * FROM categories ORDER BY type, parentCategoryId IS NOT NULL, name")
    fun observeAll(): Flow<List<CategoryEntity>>

    @Query("SELECT * FROM categories WHERE id = :id")
    suspend fun getById(id: Long): CategoryEntity?

    @Query("SELECT * FROM categories WHERE type = :type")
    fun observeByType(type: CategoryType): Flow<List<CategoryEntity>>

    @Query("SELECT COUNT(*) FROM transactions WHERE categoryId = :id")
    suspend fun transactionCount(id: Long): Int
}