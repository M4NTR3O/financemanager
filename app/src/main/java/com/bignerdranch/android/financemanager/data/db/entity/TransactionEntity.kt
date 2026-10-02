package com.bignerdranch.android.financemanager.data.db.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "transactions",
    foreignKeys = [
        ForeignKey(UserEntity::class, ["id"], ["userId"], onDelete = ForeignKey.CASCADE),
        ForeignKey(CategoryEntity::class, ["id"], ["categoryId"], onDelete = ForeignKey.RESTRICT)
    ],
    indices = [Index("userId"), Index("categoryId"), Index("dateTime")]
)
data class TransactionEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val userId: Long,
    val categoryId: Long,
    val amountMinor: Long,   // всегда положительное
    val dateTime: Long,      // epoch millis
    val comment: String?
)