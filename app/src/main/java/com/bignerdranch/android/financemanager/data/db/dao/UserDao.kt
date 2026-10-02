package com.bignerdranch.android.financemanager.data.db.dao

import androidx.room.*
import com.bignerdranch.android.financemanager.data.db.entity.UserEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface UserDao {
    @Insert suspend fun insert(user: UserEntity): Long
    @Update suspend fun update(user: UserEntity)
    @Query("SELECT * FROM users ORDER BY id LIMIT 1") fun observeFirst(): Flow<UserEntity?>
    @Query("SELECT * FROM users ORDER BY id LIMIT 1") suspend fun getFirst(): UserEntity?
}