package com.babacode.walletexpensetracker.ui.compose.charts

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.babacode.walletexpensetracker.ui.theme.WalletExpenseTheme
import com.babacode.walletexpensetracker.ui.theme.WalletTheme

data class BarChartEntry(val label: String, val value: Float)

// Generic labeled bar chart with rounded-top bars and per-bar coloring, matching
// the reference design's recharts BarChart usage (refrence/src/routes/detail.tsx,
// refrence/src/routes/insights.tsx) — used for the Detail-screen period trend and
// the Insights 6-month trend, which each supply their own highlight rule via [barColor].
@Composable
fun BarChart(
    entries: List<BarChartEntry>,
    modifier: Modifier = Modifier,
    barsHeight: Dp = 112.dp,
    showLabels: Boolean = true,
    barColor: @Composable (index: Int, entry: BarChartEntry) -> Color = { _, _ ->
        MaterialTheme.colorScheme.primary
    }
) {
    val maxValue = entries.maxOfOrNull { it.value } ?: 0f

    Column(modifier = modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(barsHeight),
            horizontalArrangement = Arrangement.spacedBy(4.dp),
            verticalAlignment = Alignment.Bottom
        ) {
            entries.forEachIndexed { index, entry ->
                val heightFraction = if (maxValue > 0f) (entry.value / maxValue).coerceIn(0f, 1f) else 0f
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight(heightFraction)
                        .background(
                            color = barColor(index, entry),
                            shape = RoundedCornerShape(topStart = 4.dp, topEnd = 4.dp)
                        )
                )
            }
        }

        if (showLabels) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = WalletTheme.spacing.extraSmall),
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                entries.forEach { entry ->
                    Text(
                        text = entry.label,
                        modifier = Modifier.weight(1f),
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        textAlign = TextAlign.Center,
                        maxLines = 1
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun BarChartPreview() {
    WalletExpenseTheme {
        BarChart(
            entries = listOf(
                BarChartEntry("Mon", 40f),
                BarChartEntry("Tue", 80f),
                BarChartEntry("Wed", 55f),
                BarChartEntry("Thu", 20f),
                BarChartEntry("Fri", 95f),
                BarChartEntry("Sat", 65f),
                BarChartEntry("Sun", 30f)
            ),
            barColor = { index, _ ->
                if (index == 4) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.secondary
            },
            modifier = Modifier.padding(16.dp)
        )
    }
}
