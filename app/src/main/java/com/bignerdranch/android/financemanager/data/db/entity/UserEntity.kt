package com.bignerdranch.android.financemanager.data.db.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "users")
data class UserEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val createdAt: Long,           // epoch millis
    val initialBalanceMinor: Long  // в копейках
)