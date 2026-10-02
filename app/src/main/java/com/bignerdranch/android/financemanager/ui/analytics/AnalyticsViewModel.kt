package com.bignerdranch.android.financemanager.ui.analytics

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bignerdranch.android.financemanager.domain.analytics.*
import com.bignerdranch.android.financemanager.domain.model.Category
import com.bignerdranch.android.financemanager.domain.model.CategoryType
import com.bignerdranch.android.financemanager.domain.model.Transaction
import com.bignerdranch.android.financemanager.domain.repository.FinanceRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import java.math.BigDecimal
import javax.inject.Inject

data class AnalyticsState(
    val totalIncome: BigDecimal = BigDecimal.ZERO,
    val totalExpense: BigDecimal = BigDecimal.ZERO,
    val balance: BigDecimal = BigDecimal.ZERO,
    val expenseByCategory: List<CategorySlice> = emptyList(),
    val monthly: List<MonthlyPoint> = emptyList(),
    val averages: MonthlyAverages = MonthlyAverages(BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO, 0)
)

@HiltViewModel
class AnalyticsViewModel @Inject constructor(
    repo: FinanceRepository
) : ViewModel() {

    val state: StateFlow<AnalyticsState> = combine(
        repo.observeUser(), repo.observeTransactions(), repo.observeCategories()
    ) { user, txs, cats ->
        val map = cats.associateBy { it.id }
        val income = BalanceCalculator.totalIncome(txs, map)
        val expense = BalanceCalculator.totalExpense(txs, map)
        val balance = BalanceCalculator.balance(
            user?.initialBalance ?: BigDecimal.ZERO, txs, map)
        val monthly = AnalyticsCalculator.monthlyPoints(txs, map)
        AnalyticsState(
            totalIncome = income,
            totalExpense = expense,
            balance = balance,
            expenseByCategory = AnalyticsCalculator.byCategory(txs, map, CategoryType.Expense),
            monthly = monthly,
            averages = AnalyticsCalculator.averages(monthly)
        )
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), AnalyticsState())
}