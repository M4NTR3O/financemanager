package com.bignerdranch.android.financemanager.ui.setup

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.bignerdranch.android.financemanager.ui.common.AmountField

@Composable
fun SetupScreen(vm: SetupViewModel) {
    val error by vm.error.collectAsStateWithLifecycle()
    var value by remember { mutableStateOf("") }

    Surface(Modifier.fillMaxSize()) {
        Column(
            Modifier.fillMaxSize().padding(24.dp),
            verticalArrangement = Arrangement.Center
        ) {
            Text("Начальный баланс", style = MaterialTheme.typography.headlineSmall)
            Spacer(Modifier.height(8.dp))
            Text(
                "Укажите состояние ваших средств до первой транзакции. " +
                        "Может быть отрицательным (например, если есть долг).",
                style = MaterialTheme.typography.bodyMedium
            )
            Spacer(Modifier.height(16.dp))
            AmountField(
                value = value,
                onValueChange = { value = it },
                label = "Начальный баланс",
                isError = error != null
            )
            error?.let {
                Spacer(Modifier.height(4.dp))
                Text(it, color = MaterialTheme.colorScheme.error)
            }
            Spacer(Modifier.height(24.dp))
            Button(
                onClick = { vm.submit(value) },
                enabled = value.isNotBlank(),
                modifier = Modifier.fillMaxWidth()
            ) { Text("Начать") }
        }
    }
}