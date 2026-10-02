package com.bignerdranch.android.financemanager.ui.analytics

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.bignerdranch.android.financemanager.ui.common.MoneyText
import com.bignerdranch.android.financemanager.ui.common.formatMoney
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.graphics.drawscope.Stroke
import com.bignerdranch.android.financemanager.domain.analytics.CategorySlice

@Composable
fun AnalyticsScreen(vm: AnalyticsViewModel = hiltViewModel()) {
    val s by vm.state.collectAsStateWithLifecycle()

    LazyColumn(
        Modifier.fillMaxSize().padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Card(Modifier.fillMaxWidth()) {
                Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text("Итоги", style = MaterialTheme.typography.titleMedium)
                    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text("Доходы")
                        MoneyText(s.totalIncome, color = Color(0xFF2E7D32))
                    }
                    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text("Расходы")
                        MoneyText(s.totalExpense, color = MaterialTheme.colorScheme.error)
                    }
                    HorizontalDivider()
                    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text("Баланс", style = MaterialTheme.typography.titleMedium)
                        MoneyText(s.balance, weight = androidx.compose.ui.text.font.FontWeight.Bold)
                    }
                }
            }
        }

        item {
            Text("Расходы по категориям",
                style = MaterialTheme.typography.titleMedium)
            Spacer(Modifier.height(8.dp))
            DonutChart(
                slices = s.expenseByCategory,
                modifier = Modifier.fillMaxWidth().height(240.dp)
            )
            Spacer(Modifier.height(16.dp))
            s.expenseByCategory.forEach { slice ->
                Row(
                    Modifier.fillMaxWidth().padding(vertical = 4.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(slice.category.name)
                    Text(formatMoney(slice.total))
                }
            }
        }

        item {
            Text("Средние показатели по месяцам",
                style = MaterialTheme.typography.titleMedium)
            val a = s.averages
            Card(Modifier.fillMaxWidth()) {
                Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text("Средний доход / мес.")
                        Text(formatMoney(a.avgIncome))
                    }
                    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text("Средний расход / мес.")
                        Text(formatMoney(a.avgExpense))
                    }
                    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text("Среднее сальдо / мес.")
                        Text(formatMoney(a.avgBalance))
                    }
                    Text("Учтено месяцев: ${a.months}",
                        style = MaterialTheme.typography.labelSmall)
                }
            }
        }

        item {
            Text("Доходы и расходы по месяцам",
                style = MaterialTheme.typography.titleMedium)
            val bars = s.monthly.takeLast(12).flatMap {
                listOf(
                    Bar(it.month.toString(), it.income, Color(0xFF2E7D32)),
                    Bar("", it.expense, Color(0xFFC62828))
                )
            }
            BarChart(bars = bars)
        }

        item { Spacer(Modifier.height(32.dp)) }
    }
}

@Composable
fun DonutChart(
    slices: List<CategorySlice>,
    modifier: Modifier = Modifier
) {
    if (slices.isEmpty()) {
        Box(modifier, contentAlignment = Alignment.Center) {
            Text("Нет данных", style = MaterialTheme.typography.bodySmall)
        }
        return
    }
    val total = slices.sumOf { it.total.toDouble() }.toFloat().coerceAtLeast(1f)
    val colors = listOf(
        Color(0xFF69F0AE), Color(0xFF448AFF), Color(0xFFFFAB40),
        Color(0xFFE040FB), Color(0xFFFF5252), Color(0xFF18FFFF)
    )
    Box(modifier, contentAlignment = Alignment.Center) {
        Canvas(modifier = Modifier.size(220.dp)) {
            var startAngle = -90f
            slices.forEachIndexed { i, slice ->
                val sweep = (slice.total.toFloat() / total) * 360f
                drawArc(
                    color = colors[i % colors.size],
                    startAngle = startAngle,
                    sweepAngle = sweep,
                    useCenter = false,
                    style = Stroke(width = 40.dp.toPx(), cap = StrokeCap.Butt)
                )
                startAngle += sweep
            }
        }
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text("Всего", style = MaterialTheme.typography.labelSmall)
            Text(
                formatMoney(slices.fold(java.math.BigDecimal.ZERO) { a, s -> a + s.total }),
                style = MaterialTheme.typography.titleMedium
            )
        }
    }
}