package com.bignerdranch.android.financemanager.domain.model

data class Category(
    val id: Long,
    val name: String,
    val parentCategoryId: Long?,
    val type: CategoryType,
    val isSystem: Boolean
) {
    val isRoot: Boolean get() = parentCategoryId == null
}