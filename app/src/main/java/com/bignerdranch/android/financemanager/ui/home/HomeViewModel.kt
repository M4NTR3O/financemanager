package com.bignerdranch.android.financemanager.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bignerdranch.android.financemanager.domain.analytics.BalanceCalculator
import com.bignerdranch.android.financemanager.domain.model.Category
import com.bignerdranch.android.financemanager.domain.model.Transaction
import com.bignerdranch.android.financemanager.domain.repository.FinanceRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import java.math.BigDecimal
import javax.inject.Inject

data class HomeState(
    val balance: BigDecimal = BigDecimal.ZERO,
    val recent: List<Transaction> = emptyList(),
    val categories: Map<Long, Category> = emptyMap()
)

@HiltViewModel
class HomeViewModel @Inject constructor(
    repo: FinanceRepository
) : ViewModel() {

    val state: StateFlow<HomeState> = combine(
        repo.observeUser(),
        repo.observeTransactions(),
        repo.observeCategories()
    ) { user, txs, cats ->
        val map = cats.associateBy { it.id }
        val balance = BalanceCalculator.balance(
            user?.initialBalance ?: BigDecimal.ZERO, txs, map)
        HomeState(
            balance = balance,
            recent = txs.take(10),
            categories = map
        )
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), HomeState())
}