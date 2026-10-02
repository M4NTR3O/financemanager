package com.bignerdranch.android.financemanager.domain.analytics

import com.bignerdranch.android.financemanager.domain.model.Category
import com.bignerdranch.android.financemanager.domain.model.CategoryType
import com.bignerdranch.android.financemanager.domain.model.Transaction
import java.math.BigDecimal
import java.math.RoundingMode
import java.time.YearMonth
import java.time.ZoneId
import java.time.temporal.WeekFields

data class CategorySlice(val category: Category, val total: BigDecimal)
data class MonthlyPoint(val month: YearMonth, val income: BigDecimal, val expense: BigDecimal) {
    val balance: BigDecimal get() = income - expense
}
data class MonthlyAverages(
    val avgIncome: BigDecimal,
    val avgExpense: BigDecimal,
    val avgBalance: BigDecimal,
    val months: Int
)

enum class Grouping { Day, Week, Month, Year }

object AnalyticsCalculator {

    fun byCategory(
        txs: List<Transaction>,
        categories: Map<Long, Category>,
        type: CategoryType
    ): List<CategorySlice> =
        txs.asSequence()
            .mapNotNull { t -> categories[t.categoryId]?.takeIf { it.type == type }?.let { it to t } }
            .groupBy({ it.first }, { it.second.amount })
            .map { (cat, amounts) -> CategorySlice(cat, amounts.fold(BigDecimal.ZERO) { a, b -> a + b }) }
            .sortedByDescending { it.total }

    fun monthlyPoints(
        txs: List<Transaction>,
        categories: Map<Long, Category>,
        zone: ZoneId = ZoneId.systemDefault()
    ): List<MonthlyPoint> {
        val grouped = txs.groupBy { YearMonth.from(it.dateTime.atZone(zone)) }
        return grouped.entries
            .map { (ym, list) ->
                val income = list.filter { categories[it.categoryId]?.type == CategoryType.Income }
                    .fold(BigDecimal.ZERO) { a, t -> a + t.amount }
                val expense = list.filter { categories[it.categoryId]?.type == CategoryType.Expense }
                    .fold(BigDecimal.ZERO) { a, t -> a + t.amount }
                MonthlyPoint(ym, income, expense)
            }
            .sortedBy { it.month }
    }

    fun averages(points: List<MonthlyPoint>): MonthlyAverages {
        if (points.isEmpty()) return MonthlyAverages(BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO, 0)
        val n = points.size.toBigDecimal()
        fun avg(sel: (MonthlyPoint) -> BigDecimal) =
            points.fold(BigDecimal.ZERO) { a, p -> a + sel(p) }
                .divide(n, 2, RoundingMode.HALF_UP)
        return MonthlyAverages(
            avgIncome = avg { it.income },
            avgExpense = avg { it.expense },
            avgBalance = avg { it.balance },
            months = points.size
        )
    }

    fun bucket(
        txs: List<Transaction>,
        grouping: Grouping,
        zone: ZoneId = ZoneId.systemDefault()
    ): List<Pair<String, BigDecimal>> {
        val weekFields = WeekFields.ISO
        val keyer: (Transaction) -> String = { t ->
            val z = t.dateTime.atZone(zone)
            when (grouping) {
                Grouping.Day -> "%04d-%02d-%02d".format(z.year, z.monthValue, z.dayOfMonth)
                Grouping.Week -> {
                    val w = z.get(weekFields.weekOfWeekBasedYear())
                    "%04d-W%02d".format(z.get(weekFields.weekBasedYear()), w)
                }
                Grouping.Month -> "%04d-%02d".format(z.year, z.monthValue)
                Grouping.Year -> "%04d".format(z.year)
            }
        }
        return txs.groupBy(keyer)
            .map { (k, list) -> k to list.fold(BigDecimal.ZERO) { a, t -> a + t.amount } }
            .sortedBy { it.first }
    }
}