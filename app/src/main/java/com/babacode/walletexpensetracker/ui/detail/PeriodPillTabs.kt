package com.babacode.walletexpensetracker.ui.detail

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.babacode.walletexpensetracker.ui.theme.ShapeExtraLarge
import com.babacode.walletexpensetracker.ui.theme.ShapeMedium
import com.babacode.walletexpensetracker.ui.theme.WalletTheme

// Segmented pill period selector, matching the reference design's tab bar
// (refrence/src/routes/detail.tsx) — drives (and is driven by) the HorizontalPager
// in TransactionTypeScreen, which remains the swipe-gesture layer underneath.
@Composable
fun PeriodPillTabs(
    labels: List<String>,
    selectedIndex: Int,
    onSelected: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .background(color = MaterialTheme.colorScheme.secondary, shape = ShapeExtraLarge)
            .padding(4.dp),
        horizontalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        labels.forEachIndexed { index, label ->
            val selected = index == selectedIndex
            Box(
                modifier = Modifier
                    .weight(1f)
                    .clip(ShapeMedium)
                    .background(if (selected) MaterialTheme.colorScheme.background else Color.Transparent)
                    .clickable { onSelected(index) }
                    .padding(vertical = WalletTheme.spacing.small),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = label,
                    style = MaterialTheme.typography.labelMedium,
                    color = if (selected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}
