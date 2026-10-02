package com.bignerdranch.android.financemanager.data.db.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import com.bignerdranch.android.financemanager.domain.model.CategoryType

@Entity(
    tableName = "categories",
    foreignKeys = [
        ForeignKey(
            entity = CategoryEntity::class,
            parentColumns = ["id"],
            childColumns = ["parentCategoryId"],
            onDelete = ForeignKey.SET_NULL // поведение родителя — «дети становятся корневыми»
        )
    ],
    indices = [Index("parentCategoryId"), Index("type")]
)
data class CategoryEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val name: String,
    val parentCategoryId: Long?,
    val type: CategoryType,
    val isSystem: Boolean
)