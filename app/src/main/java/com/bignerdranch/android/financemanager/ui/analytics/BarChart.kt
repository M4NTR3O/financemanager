package com.bignerdranch.android.financemanager.ui.analytics

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.bignerdranch.android.financemanager.ui.common.formatMoney
import java.math.BigDecimal

data class Bar(val label: String, val value: BigDecimal, val color: Color)

@Composable
fun BarChart(
    bars: List<Bar>,
    modifier: Modifier = Modifier,
    height: androidx.compose.ui.unit.Dp = 200.dp
) {
    if (bars.isEmpty()) {
        Box(Modifier.fillMaxWidth().height(height), contentAlignment = Alignment.Center) {
            Text("Нет данных", style = MaterialTheme.typography.bodySmall)
        }
        return
    }
    val maxV = bars.maxOf { it.value }.coerceAtLeast(BigDecimal.ONE).toFloat()
    Column(modifier) {
        Canvas(Modifier.fillMaxWidth().height(height)) {
            val n = bars.size
            val gap = 8.dp.toPx()
            val barW = (size.width - gap * (n - 1)) / n
            bars.forEachIndexed { i, b ->
                val h = (b.value.toFloat() / maxV) * size.height
                drawRect(
                    color = b.color,
                    topLeft = Offset(i * (barW + gap), size.height - h),
                    size = Size(barW, h)
                )
            }
        }
        Spacer(Modifier.height(4.dp))
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            bars.forEach {
                Text(it.label, style = MaterialTheme.typography.labelSmall,
                    modifier = Modifier.weight(1f))
            }
        }
    }
}