package com.babacode.walletexpensetracker.ui.compose

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.drawOutline
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.babacode.walletexpensetracker.ui.theme.ShapeExtraLarge
import com.babacode.walletexpensetracker.ui.theme.WalletTheme

// Reusable placeholder shown on every list screen with no data, matching the
// reference design's dashed-border EmptyState (refrence/src/components/app/TransactionRow.tsx).
@Composable
fun EmptyState(
    message: String,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = WalletTheme.spacing.huge)
            .dashedBorder(
                color = MaterialTheme.colorScheme.outline,
                shape = ShapeExtraLarge
            )
            .padding(WalletTheme.spacing.extraLarge),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = message,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center
        )
    }
}

private fun Modifier.dashedBorder(
    color: Color,
    shape: Shape,
    strokeWidth: Dp = 1.dp,
    dashLength: Dp = 6.dp,
    gapLength: Dp = 4.dp
): Modifier = this.drawWithContent {
    drawContent()
    val outline = shape.createOutline(Size(size.width, size.height), layoutDirection, this)
    drawOutline(
        outline = outline,
        color = color,
        style = Stroke(
            width = strokeWidth.toPx(),
            pathEffect = PathEffect.dashPathEffect(floatArrayOf(dashLength.toPx(), gapLength.toPx()), 0f)
        )
    )
}
