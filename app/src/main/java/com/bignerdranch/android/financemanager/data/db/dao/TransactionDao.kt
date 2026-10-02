package com.bignerdranch.android.financemanager.data.db.dao

import androidx.room.*
import com.bignerdranch.android.financemanager.data.db.entity.TransactionEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface TransactionDao {
    @Insert suspend fun insert(t: TransactionEntity): Long
    @Update suspend fun update(t: TransactionEntity)
    @Delete suspend fun delete(t: TransactionEntity)

    @Query("SELECT * FROM transactions WHERE id = :id")
    suspend fun getById(id: Long): TransactionEntity?

    @Query("SELECT * FROM transactions ORDER BY dateTime DESC")
    fun observeAll(): Flow<List<TransactionEntity>>

    @Query("SELECT * FROM transactions WHERE dateTime BETWEEN :from AND :to ORDER BY dateTime DESC")
    fun observeBetween(from: Long, to: Long): Flow<List<TransactionEntity>>

    @Query("SELECT * FROM transactions ORDER BY dateTime DESC")
    suspend fun getAllOnce(): List<TransactionEntity>
}