package com.bignerdranch.android.financemanager.ui.transactions

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.bignerdranch.android.financemanager.domain.model.CategoryType
import com.bignerdranch.android.financemanager.ui.common.MoneyText
import java.time.ZoneId
import java.time.format.DateTimeFormatter

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TransactionListScreen(
    onEdit: (Long) -> Unit,
    onAdd: () -> Unit,
    vm: TransactionListViewModel = hiltViewModel()
) {
    val s by vm.state.collectAsStateWithLifecycle()
    var pendingDelete by remember { mutableStateOf<Long?>(null) }

    Scaffold(
        topBar = { TopAppBar(title = { Text("Операции") }) },
        floatingActionButton = {
            FloatingActionButton(onClick = onAdd) { Icon(Icons.Filled.Add, null) }
        }
    ) { pad ->
        if (s.transactions.isEmpty()) {
            Box(Modifier.fillMaxSize().padding(pad), contentAlignment = androidx.compose.ui.Alignment.Center) {
                Text("Нет операций")
            }
        } else {
            LazyColumn(Modifier.fillMaxSize().padding(pad)) {
                items(s.transactions, key = { it.id }) { tx ->
                    val cat = s.categories[tx.categoryId]
                    ListItem(
                        modifier = Modifier.clickable { onEdit(tx.id) },
                        headlineContent = { Text(cat?.name ?: "—") },
                        supportingContent = {
                            Text(tx.dateTime.atZone(ZoneId.systemDefault())
                                .format(DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm")) +
                                    (tx.comment?.let { " • $it" } ?: ""))
                        },
                        trailingContent = {
                            Row(verticalAlignment = androidx.compose.ui.Alignment.CenterVertically) {
                                MoneyText(
                                    value = tx.amount, signed = true,
                                    color = if (cat?.type == CategoryType.Income)
                                        Color(0xFF2E7D32) else MaterialTheme.colorScheme.error
                                )
                                IconButton(onClick = { pendingDelete = tx.id }) {
                                    Icon(Icons.Filled.Delete, "Удалить")
                                }
                            }
                        }
                    )
                    HorizontalDivider()
                }
            }
        }
    }

    pendingDelete?.let { id ->
        val tx = s.transactions.firstOrNull { it.id == id }
        if (tx != null) {
            AlertDialog(
                onDismissRequest = { pendingDelete = null },
                title = { Text("Удалить операцию?") },
                text = { Text("Это действие нельзя отменить.") },
                confirmButton = {
                    TextButton(onClick = { vm.delete(tx); pendingDelete = null }) { Text("Удалить") }
                },
                dismissButton = {
                    TextButton(onClick = { pendingDelete = null }) { Text("Отмена") }
                }
            )
        }
    }
}