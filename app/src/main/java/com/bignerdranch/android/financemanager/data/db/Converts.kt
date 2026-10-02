package com.bignerdranch.android.financemanager.data.db

import androidx.room.TypeConverter
import com.bignerdranch.android.financemanager.domain.model.CategoryType

class Converters {
    @TypeConverter fun fromCategoryType(t: CategoryType): String = t.name
    @TypeConverter fun toCategoryType(s: String): CategoryType = CategoryType.valueOf(s)
}