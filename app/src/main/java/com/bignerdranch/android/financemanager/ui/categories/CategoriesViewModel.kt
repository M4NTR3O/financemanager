package com.bignerdranch.android.financemanager.ui.categories

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bignerdranch.android.financemanager.domain.model.Category
import com.bignerdranch.android.financemanager.domain.model.CategoryType
import com.bignerdranch.android.financemanager.domain.repository.CategoryDeleteResult
import com.bignerdranch.android.financemanager.domain.repository.FinanceRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

data class CatsState(
    val categories: List<Category> = emptyList(),
    val message: String? = null
)

@HiltViewModel
class CategoriesViewModel @Inject constructor(
    private val repo: FinanceRepository
) : ViewModel() {

    val state: StateFlow<CatsState> = repo.observeCategories()
        .map { CatsState(it) }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), CatsState())

    fun add(name: String, parentId: Long?, type: CategoryType) {
        if (name.isBlank()) return
        viewModelScope.launch { repo.addCategory(name.trim(), parentId, type) }
    }

    fun delete(id: Long) = viewModelScope.launch {
        when (val r = repo.deleteCategory(id)) {
            CategoryDeleteResult.SystemCategory -> show("Системную категорию удалить нельзя")
            CategoryDeleteResult.HasTransactions -> show("Есть связанные операции — удаление запрещено")
            CategoryDeleteResult.Ok -> show("Удалено")
        }
    }

    private fun show(msg: String) { /* обрабатывается на экране через Snackbar */ }
}