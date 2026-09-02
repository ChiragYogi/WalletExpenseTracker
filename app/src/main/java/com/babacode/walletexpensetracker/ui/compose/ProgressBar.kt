package com.babacode.walletexpensetracker.ui.compose

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.unit.dp
import com.babacode.walletexpensetracker.ui.theme.WalletTheme

// Threshold-colored progress bar used for budget usage (<80% primary, 80-100%
// warning, over expense-red) and other ratio displays, matching the reference
// design's budget progress bars (refrence/src/routes/budgets.tsx).
@Composable
fun ThresholdProgressBar(
    progress: Float,
    modifier: Modifier = Modifier,
    warningThreshold: Float = 0.8f
) {
    val extendedColors = WalletTheme.extendedColors
    val color = when {
        progress > 1f -> extendedColors.expense
        progress >= warningThreshold -> extendedColors.warning
        else -> MaterialTheme.colorScheme.primary
    }
    ProgressBar(progress = progress.coerceIn(0f, 1f), color = color, modifier = modifier)
}

@Composable
fun ProgressBar(
    progress: Float,
    modifier: Modifier = Modifier,
    color: Color = MaterialTheme.colorScheme.primary
) {
    LinearProgressIndicator(
        progress = { progress.coerceIn(0f, 1f) },
        modifier = modifier
            .fillMaxWidth()
            .height(8.dp)
            .clip(RoundedCornerShape(4.dp)),
        color = color,
        trackColor = MaterialTheme.colorScheme.surfaceVariant,
        strokeCap = StrokeCap.Round
    )
}
