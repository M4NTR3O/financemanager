package com.bignerdranch.android.financemanager.domain.model

import java.math.BigDecimal
import java.time.Instant

data class Transaction(
    val id: Long,
    val userId: Long,
    val categoryId: Long,
    val amount: BigDecimal,   // положительная величина
    val dateTime: Instant,
    val comment: String?
)