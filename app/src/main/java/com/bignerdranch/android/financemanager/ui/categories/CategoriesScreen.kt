package com.bignerdranch.android.financemanager.ui.categories

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
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.bignerdranch.android.financemanager.domain.model.CategoryType

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CategoriesScreen(vm: CategoriesViewModel = hiltViewModel()) {
    val s by vm.state.collectAsStateWithLifecycle()
    var showDialog by remember { mutableStateOf(false) }

    Scaffold(
        topBar = { TopAppBar(title = { Text("Категории") }) },
        floatingActionButton = {
            FloatingActionButton(onClick = { showDialog = true }) { Icon(Icons.Filled.Add, null) }
        }
    ) { pad ->
        LazyColumn(Modifier.fillMaxSize().padding(pad)) {
            val byType = s.categories.groupBy { it.type }
            listOf(CategoryType.Expense, CategoryType.Income).forEach { type ->
                val cats = byType[type].orEmpty()
                val roots = cats.filter { it.parentCategoryId == null }
                item(key = "h-$type") {
                    Text(
                        if (type == CategoryType.Expense) "Расходы" else "Доходы",
                        style = MaterialTheme.typography.titleMedium,
                        modifier = Modifier.padding(16.dp)
                    )
                }
                roots.forEach { root ->
                    item(key = "r-${root.id}") {
                        ListItem(
                            headlineContent = { Text(root.name) },
                            trailingContent = {
                                if (!root.isSystem) {
                                    IconButton(onClick = { vm.delete(root.id) }) {
                                        Icon(Icons.Filled.Delete, "Удалить")
                                    }
                                } else {
                                    Text("системная", style = MaterialTheme.typography.labelSmall)
                                }
                            }
                        )
                    }
                    cats.filter { it.parentCategoryId == root.id }.forEach { child ->
                        item(key = "c-${child.id}") {
                            ListItem(
                                headlineContent = { Text("   ↳ ${child.name}") },
                                trailingContent = {
                                    if (!child.isSystem) {
                                        IconButton(onClick = { vm.delete(child.id) }) {
                                            Icon(Icons.Filled.Delete, "Удалить")
                                        }
                                    }
                                }
                            )
                        }
                    }
                }
            }
        }
    }

    if (showDialog) {
        AddCategoryDialog(
            categories = s.categories,
            onDismiss = { showDialog = false },
            onConfirm = { name, parentId, type ->
                vm.add(name, parentId, type); showDialog = false
            }
        )
    }
}

@Composable
private fun AddCategoryDialog(
    categories: List<com.bignerdranch.android.financemanager.domain.model.Category>,
    onDismiss: () -> Unit,
    onConfirm: (String, Long?, CategoryType) -> Unit
) {
    var name by remember { mutableStateOf("") }
    var type by remember { mutableStateOf(CategoryType.Expense) }
    var parentId by remember { mutableStateOf<Long?>(null) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Новая категория") },
        text = {
            Column {
                Row {
                    FilterChip(selected = type == CategoryType.Expense,
                        onClick = { type = CategoryType.Expense; parentId = null },
                        label = { Text("Расход") }, modifier = Modifier.padding(end = 8.dp))
                    FilterChip(selected = type == CategoryType.Income,
                        onClick = { type = CategoryType.Income; parentId = null },
                        label = { Text("Доход") })
                }
                Spacer(Modifier.height(8.dp))
                OutlinedTextField(value = name, onValueChange = { name = it },
                    label = { Text("Название") }, singleLine = true,
                    modifier = Modifier.fillMaxWidth())
                Spacer(Modifier.height(8.dp))
                Text("Родительская категория", style = MaterialTheme.typography.labelMedium)
                val roots = categories.filter { it.type == type && it.parentCategoryId == null }
                LazyColumn(Modifier.heightIn(max = 200.dp)) {
                    item {
                        ListItem(
                            headlineContent = { Text("— Нет (корневая)") },
                            modifier = Modifier.clickable { parentId = null }
                        )
                    }
                    items(roots, key = { it.id }) { r ->
                        ListItem(
                            headlineContent = { Text(r.name) },
                            modifier = Modifier.clickable { parentId = r.id }
                        )
                    }
                }
                if (parentId != null) {
                    Text("Выбрано: ${categories.first { it.id == parentId }.name}",
                        style = MaterialTheme.typography.labelSmall)
                }
            }
        },
        confirmButton = {
            TextButton(onClick = { onConfirm(name, parentId, type) }, enabled = name.isNotBlank()) {
                Text("Создать")
            }
        },
        dismissButton = { TextButton(onClick = onDismiss) { Text("Отмена") } }
    )
}