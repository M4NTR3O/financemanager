package com.bignerdranch.android.financemanager.ui.home

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.bignerdranch.android.financemanager.domain.model.CategoryType
import com.bignerdranch.android.financemanager.ui.common.MoneyText
import java.time.ZoneId
import java.time.format.DateTimeFormatter

@Composable
fun HomeScreen(
    onOpenTransactions: () -> Unit,
    onAddTransaction: () -> Unit,
    vm: HomeViewModel = hiltViewModel()
) {
    val s by vm.state.collectAsStateWithLifecycle()

    Scaffold(
        floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick = onAddTransaction,
                icon = { Icon(Icons.Filled.Add, null) },
                text = { Text("Операция") }
            )
        }
    ) { pad ->
        Column(Modifier.fillMaxSize().padding(pad).padding(16.dp)) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = androidx.compose.foundation.shape.RoundedCornerShape(20.dp),
                colors = androidx.compose.material3.CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f)
                ),
                elevation = androidx.compose.material3.CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(Modifier.padding(20.dp)) {
                    Text("Текущий баланс", style = MaterialTheme.typography.labelLarge)
                    Spacer(Modifier.height(4.dp))
                    MoneyText(
                        value = s.balance,
                        fontSize = 40.sp(),
                        weight = FontWeight.Bold,
                        color = if (s.balance.signum() < 0) MaterialTheme.colorScheme.error
                        else MaterialTheme.colorScheme.onSurface
                    )
                }
            }
            Spacer(Modifier.height(16.dp))
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically) {
                Text("Последние операции", style = MaterialTheme.typography.titleMedium)
                TextButton(onClick = onOpenTransactions) { Text("Все") }
            }
            if (s.recent.isEmpty()) {
                Box(Modifier.fillMaxWidth().padding(32.dp), contentAlignment = Alignment.Center) {
                    Text("Пока нет операций", style = MaterialTheme.typography.bodyMedium)
                }
            } else {
                LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    items(s.recent, key = { it.id }) { tx ->
                        val cat = s.categories[tx.categoryId]
                        ListItem(
                            headlineContent = { Text(cat?.name ?: "—") },
                            supportingContent = {
                                Text(tx.dateTime.atZone(ZoneId.systemDefault())
                                    .format(DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm")) +
                                        (tx.comment?.let { " • $it" } ?: ""))
                            },
                            trailingContent = {
                                MoneyText(
                                    value = tx.amount, signed = true,
                                    color = if (cat?.type == CategoryType.Income)
                                        Color(0xFF2E7D32) else MaterialTheme.colorScheme.error
                                )
                            }
                        )
                    }
                }
            }
        }
    }
}

private fun Int.sp() = androidx.compose.ui.unit.TextUnit(
    this.toFloat(), androidx.compose.ui.unit.TextUnitType.Sp)