package com.bignerdranch.android.financemanager.data.db.seed

import com.bignerdranch.android.financemanager.data.db.entity.CategoryEntity
import com.bignerdranch.android.financemanager.domain.model.CategoryType

/**
 * Системные категории создаются один раз при первом запуске.
 * Родители идут первыми — дочерние ссылаются на их id после вставки.
 */
object SystemCategories {
    // Возвращает пары (родительское имя, дочерние имена)
    val expense: List<Pair<String, List<String>>> = listOf(
        "Еда" to listOf("Продукты", "Рестораны", "Доставка"),
        "Транспорт" to listOf("Общественный", "Такси", "Авто"),
        "Жильё" to listOf("Аренда", "Коммуналка", "Интернет"),
        "Здоровье" to listOf("Аптека", "Врачи"),
        "Развлечения" to listOf("Кино", "Подписки"),
        "Прочее" to emptyList()
    )
    val income: List<Pair<String, List<String>>> = listOf(
        "Зарплата" to emptyList(),
        "Подработка" to emptyList(),
        "Подарки" to emptyList(),
        "Прочее" to emptyList()
    )
}