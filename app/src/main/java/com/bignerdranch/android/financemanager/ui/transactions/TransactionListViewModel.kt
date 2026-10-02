package com.bignerdranch.android.financemanager.ui.transactions

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bignerdranch.android.financemanager.domain.model.Category
import com.bignerdranch.android.financemanager.domain.model.Transaction
import com.bignerdranch.android.financemanager.domain.repository.FinanceRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import javax.inject.Inject
import kotlinx.coroutines.launch

data class TxListState(
    val transactions: List<Transaction> = emptyList(),
    val categories: Map<Long, Category> = emptyMap()
)

@HiltViewModel
class TransactionListViewModel @Inject constructor(
    private val repo: FinanceRepository
) : ViewModel() {

    val state: StateFlow<TxListState> = combine(
        repo.observeTransactions(), repo.observeCategories()
    ) { txs, cats -> TxListState(txs, cats.associateBy { it.id }) }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), TxListState())

    fun delete(t: Transaction) = viewModelScope.launch { repo.deleteTransaction(t) }
}