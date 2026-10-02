package com.bignerdranch.android.financemanager.ui.common

import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.text.input.KeyboardType

@Composable
fun AmountField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String = "Сумма",
    isError: Boolean = false
) {
    OutlinedTextField(
        value = value,
        onValueChange = { raw ->
            // Позволяем только цифры и один разделитель
            val filtered = raw.filter { it.isDigit() || it == '.' || it == ',' }
                .replace(',', '.')
            val parts = filtered.split('.')
            val norm = if (parts.size > 2) parts[0] + "." + parts.drop(1).joinToString("")
            else filtered
            onValueChange(norm)
        },
        label = { Text(label) },
        singleLine = true,
        isError = isError,
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal)
    )
}