package com.bignerdranch.android.financemanager.ui.transactions

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.bignerdranch.android.financemanager.domain.model.CategoryType
import com.bignerdranch.android.financemanager.ui.common.AmountField
import java.time.ZoneId
import java.time.format.DateTimeFormatter

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TransactionEditScreen(
    txId: Long?,
    onDone: () -> Unit,
    vm: TransactionEditViewModel = hiltViewModel()
) {
    val s by vm.state.collectAsStateWithLifecycle()

    LaunchedEffect(s.saved) { if (s.saved) onDone() }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(if (s.isEdit) "Редактировать" else "Новая операция") },
                navigationIcon = {
                    IconButton(onClick = onDone) { Icon(Icons.Filled.ArrowBack, "Назад") }
                }
            )
        }
    ) { pad ->
        Column(Modifier.fillMaxSize().padding(pad).padding(16.dp)) {
            // Тип
            Row(Modifier.selectableGroup()) {
                FilterChip(
                    selected = s.type == CategoryType.Expense,
                    onClick = { vm.onType(CategoryType.Expense) },
                    label = { Text("Расход") },
                    modifier = Modifier.padding(end = 8.dp)
                )
                FilterChip(
                    selected = s.type == CategoryType.Income,
                    onClick = { vm.onType(CategoryType.Income) },
                    label = { Text("Доход") }
                )
            }
            Spacer(Modifier.height(12.dp))

            AmountField(value = s.amount, onValueChange = vm::onAmount)

            Spacer(Modifier.height(12.dp))
            Text("Категория", style = MaterialTheme.typography.labelLarge)
            Spacer(Modifier.height(4.dp))
            val filtered = s.categories.filter { it.type == s.type }
            val grouped = filtered.groupBy { it.parentCategoryId }
            LazyColumn(Modifier.heightIn(max = 240.dp)) {
                grouped[null]?.forEach { root ->
                    item(key = "root-${root.id}") {
                        ListItem(
                            headlineContent = { Text(root.name) },
                            modifier = Modifier.clickable { vm.onCategory(root.id) },
                            colors = if (s.categoryId == root.id)
                                ListItemDefaults.colors(containerColor = MaterialTheme.colorScheme.secondaryContainer)
                            else ListItemDefaults.colors()
                        )
                    }
                    grouped[root.id]?.forEach { child ->
                        item(key = "child-${child.id}") {
                            ListItem(
                                headlineContent = { Text("   ↳ ${child.name}") },
                                modifier = Modifier.clickable { vm.onCategory(child.id) },
                                colors = if (s.categoryId == child.id)
                                    ListItemDefaults.colors(containerColor = MaterialTheme.colorScheme.secondaryContainer)
                                else ListItemDefaults.colors()
                            )
                        }
                    }
                }
            }

            Spacer(Modifier.height(12.dp))
            OutlinedTextField(
                value = s.dateTime.atZone(ZoneId.systemDefault())
                    .format(DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm")),
                onValueChange = {},
                readOnly = true,
                label = { Text("Дата и время") },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(Modifier.height(12.dp))
            OutlinedTextField(
                value = s.comment,
                onValueChange = vm::onComment,
                label = { Text("Комментарий (необязательно)") },
                modifier = Modifier.fillMaxWidth()
            )

            s.error?.let {
                Spacer(Modifier.height(8.dp))
                Text(it, color = MaterialTheme.colorScheme.error)
            }

            Spacer(Modifier.weight(1f))
            Button(
                onClick = vm::save,
                modifier = Modifier.fillMaxWidth(),
                enabled = s.amount.isNotBlank() && s.categoryId != null
            ) { Text("Сохранить") }
        }
    }
}