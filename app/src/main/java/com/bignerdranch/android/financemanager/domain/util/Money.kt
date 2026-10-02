package com.bignerdranch.android.financemanager.domain.util

import java.math.BigDecimal
import java.math.RoundingMode

private const val SCALE = 2

fun BigDecimal.toMinorUnits(): Long =
    movePointRight(SCALE).setScale(0, RoundingMode.HALF_UP).longValueExact()

fun Long.toBigDecimalMoney(): BigDecimal =
    BigDecimal.valueOf(this).movePointLeft(SCALE)

fun BigDecimal.money(): BigDecimal = setScale(SCALE, RoundingMode.HALF_UP)