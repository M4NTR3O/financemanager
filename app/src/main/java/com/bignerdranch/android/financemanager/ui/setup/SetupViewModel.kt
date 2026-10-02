package com.bignerdranch.android.financemanager.ui.setup

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bignerdranch.android.financemanager.domain.repository.FinanceRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.math.BigDecimal
import javax.inject.Inject

@HiltViewModel
class SetupViewModel @Inject constructor(
    private val repo: FinanceRepository
) : ViewModel() {

    private val _ready = MutableStateFlow(false)
    val ready = _ready.asStateFlow()

    private val _userExists = MutableStateFlow<Boolean?>(null)
    val userExists = _userExists.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error = _error.asStateFlow()

    init {
        viewModelScope.launch {
            _userExists.value = repo.getUser() != null
            _ready.value = true
        }
    }

    fun submit(initialBalanceRaw: String) {
        val parsed = initialBalanceRaw.replace(',', '.').toBigDecimalOrNull()
        if (parsed == null) { _error.value = "Введите корректное число"; return }
        viewModelScope.launch {
            repo.createUser(parsed)
            _userExists.value = true
        }
    }
}