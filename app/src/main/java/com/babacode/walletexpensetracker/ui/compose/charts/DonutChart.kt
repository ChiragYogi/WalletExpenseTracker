package com.babacode.walletexpensetracker.ui.compose.charts

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.babacode.walletexpensetracker.ui.theme.WalletExpenseTheme

data class DonutSlice(val value: Float, val color: Color)

// Generic N-slice donut chart, refactored from the original 2-slice pie chart
// on Home into a reusable component (used by Home and, later, Insights).
// Matches the reference design's donut (refrence/src/routes/index.tsx: innerRadius
// 68 / outerRadius 95) rather than the original full-pie wedges, with a `centerContent`
// slot for an overlay label (e.g. net total) instead of baked-in per-slice text.
@Composable
fun DonutChart(
    slices: List<DonutSlice>,
    modifier: Modifier = Modifier,
    strokeWidthFraction: Float = 0.2f,
    emptyStateColor: Color = MaterialTheme.colorScheme.surfaceVariant,
    centerContent: @Composable BoxScope.() -> Unit = {}
) {
    val total = slices.sumOf { it.value.toDouble() }.toFloat()

    Box(modifier = modifier, contentAlignment = Alignment.Center) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val strokeWidth = size.minDimension * strokeWidthFraction
            val diameter = size.minDimension - strokeWidth
            val topLeft = Offset((size.width - diameter) / 2f, (size.height - diameter) / 2f)
            val arcSize = Size(diameter, diameter)
            val stroke = Stroke(width = strokeWidth, cap = StrokeCap.Butt)

            if (total <= 0f) {
                drawArc(
                    color = emptyStateColor,
                    startAngle = 0f,
                    sweepAngle = 360f,
                    useCenter = false,
                    topLeft = topLeft,
                    size = arcSize,
                    style = stroke
                )
            } else {
                var startAngle = -90f
                slices.forEach { slice ->
                    if (slice.value <= 0f) return@forEach
                    val sweep = (slice.value / total) * 360f
                    drawArc(
                        color = slice.color,
                        startAngle = startAngle,
                        sweepAngle = sweep,
                        useCenter = false,
                        topLeft = topLeft,
                        size = arcSize,
                        style = stroke
                    )
                    startAngle += sweep
                }
            }
        }
        centerContent()
    }
}

@Preview(showBackground = true)
@Composable
private fun DonutChartPreview() {
    WalletExpenseTheme {
        DonutChart(
            slices = listOf(
                DonutSlice(value = 6500f, color = MaterialTheme.colorScheme.error),
                DonutSlice(value = 12000f, color = MaterialTheme.colorScheme.primary)
            ),
            modifier = Modifier.size(160.dp)
        )
    }
}
