package com.babacode.walletexpensetracker.ui.compose

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import com.babacode.walletexpensetracker.ui.theme.ShapeExtraLarge
import com.babacode.walletexpensetracker.ui.theme.WalletTheme

// Small rounded "label / value" tile, matching the reference design's Stat
// sub-component (refrence/src/routes/insights-detail.tsx) — used for the
// transaction-count / average / % stat rows on Insights-Detail and Budgets.
@Composable
fun StatCard(
    label: String,
    value: String,
    modifier: Modifier = Modifier,
    valueColor: Color = MaterialTheme.colorScheme.onSurface
) {
    Card(
        modifier = modifier,
        shape = ShapeExtraLarge,
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column(modifier = Modifier.padding(WalletTheme.spacing.default)) {
            Text(
                text = label,
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(Modifier.height(WalletTheme.spacing.extraSmall))
            Text(
                text = value,
                style = MaterialTheme.typography.headlineSmall,
                color = valueColor
            )
        }
    }
}
