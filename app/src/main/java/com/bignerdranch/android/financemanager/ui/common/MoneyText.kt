package com.bignerdranch.android.financemanager.ui.common

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.sp
import java.math.BigDecimal
import java.text.NumberFormat
import java.util.Locale

private val fmt: NumberFormat = NumberFormat.getCurrencyInstance(Locale("ru", "RU"))

fun formatMoney(v: BigDecimal): String = fmt.format(v)

@Composable
fun MoneyText(
    value: BigDecimal,
    signed: Boolean = false,
    color: Color = Color.Unspecified,
    fontSize: TextUnit = 16.sp,
    weight: FontWeight = FontWeight.Normal
) {
    val prefix = if (signed && value > BigDecimal.ZERO) "+" else ""
    Text(
        text = prefix + formatMoney(value),
        color = color, fontSize = fontSize, fontWeight = weight
    )
}