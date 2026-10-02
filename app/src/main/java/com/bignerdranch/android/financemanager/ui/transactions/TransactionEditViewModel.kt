package com.bignerdranch.android.financemanager.ui.transactions

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bignerdranch.android.financemanager.domain.model.*
import com.bignerdranch.android.financemanager.domain.repository.FinanceRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.math.BigDecimal
import java.time.Instant
import javax.inject.Inject

data class EditState(
    val isEdit: Boolean = false,
    val type: CategoryType = CategoryType.Expense,
    val amount: String = "",
    val categoryId: Long? = null,
    val dateTime: Instant = Instant.now(),
    val comment: String = "",
    val categories: List<Category> = emptyList(),
    val user: User? = null,
    val saved: Boolean = false,
    val error: String? = null
)

@HiltViewModel
class TransactionEditViewModel @Inject constructor(
    private val repo: FinanceRepository,
    savedState: SavedStateHandle
) : ViewModel() {

    private val txId: Long? = savedState.get<Long>("txId")?.takeIf { it >= 0 }
    private val _state = MutableStateFlow(EditState(isEdit = txId != null))
    val state: StateFlow<EditState> = _state.asStateFlow()

    init {
        viewModelScope.launch {
            val user = repo.getUser()
            val cats = repo.observeCategories().first()
            _state.update { it.copy(user = user, categories = cats) }
            if (txId != null) {
                repo.getTransaction(txId)?.let { tx ->
                    val cat = cats.firstOrNull { it.id == tx.categoryId }
                    _state.update {
                        it.copy(
                            type = cat?.type ?: CategoryType.Expense,
                            amount = tx.amount.toPlainString(),
                            categoryId = tx.categoryId,
                            dateTime = tx.dateTime,
                            comment = tx.comment.orEmpty()
                        )
                    }
                }
            }
        }
    }

    fun onType(t: CategoryType) = _state.update { it.copy(type = t, categoryId = null) }
    fun onAmount(v: String) = _state.update { it.copy(amount = v, error = null) }
    fun onCategory(id: Long) = _state.update { it.copy(categoryId = id) }
    fun onDateTime(i: Instant) = _state.update { it.copy(dateTime = i) }
    fun onComment(v: String) = _state.update { it.copy(comment = v) }

    fun save() {
        val s = _state.value
        val amount = s.amount.replace(',', '.').toBigDecimalOrNull()
        when {
            s.user == null -> _state.update { it.copy(error = "Профиль не найден") }
            amount == null || amount <= BigDecimal.ZERO ->
                _state.update { it.copy(error = "Введите сумму больше нуля") }
            s.categoryId == null -> _state.update { it.copy(error = "Выберите категорию") }
            else -> viewModelScope.launch {
                val tx = Transaction(
                    id = txId ?: 0,
                    userId = s.user.id,
                    categoryId = s.categoryId!!,
                    amount = amount,
                    dateTime = s.dateTime,
                    comment = s.comment.ifBlank { null }
                )
                if (txId == null) repo.addTransaction(tx) else repo.updateTransaction(tx)
                _state.update { it.copy(saved = true) }
            }
        }
    }
}