package com.bignerdranch.android.financemanager.domain.model

import java.math.BigDecimal
import java.time.Instant

data class User(
    val id: Long,
    val createdAt: Instant,
    val initialBalance: BigDecimal
)