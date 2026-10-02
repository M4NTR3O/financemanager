package com.bignerdranch.android.financemanager.domain.analytics

import com.bignerdranch.android.financemanager.domain.model.Category
import com.bignerdranch.android.financemanager.domain.model.CategoryType
import com.bignerdranch.android.financemanager.domain.model.Transaction
import java.math.BigDecimal

object BalanceCalculator {

    fun totalIncome(txs: List<Transaction>, categories: Map<Long, Category>): BigDecimal =
        txs.filter { categories[it.categoryId]?.type == CategoryType.Income }
            .fold(BigDecimal.ZERO) { a, t -> a + t.amount }

    fun totalExpense(txs: List<Transaction>, categories: Map<Long, Category>): BigDecimal =
        txs.filter { categories[it.categoryId]?.type == CategoryType.Expense }
            .fold(BigDecimal.ZERO) { a, t -> a + t.amount }

    fun balance(
        initialBalance: BigDecimal,
        txs: List<Transaction>,
        categories: Map<Long, Category>
    ): BigDecimal = initialBalance + totalIncome(txs, categories) - totalExpense(txs, categories)
}