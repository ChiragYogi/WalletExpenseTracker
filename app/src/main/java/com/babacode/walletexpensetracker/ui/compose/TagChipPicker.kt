package com.babacode.walletexpensetracker.ui.compose

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.babacode.walletexpensetracker.ui.theme.WalletTheme

// Flex-wrap pill picker, matching the reference design's tag picker
// (refrence/src/components/app/TransactionForm.tsx) — the list of tags shown
// depends on the transaction type (see data/model/TagCatalog.kt). Selected tags
// use a solid primary pill; unselected tags use a soft secondary pill.
@Composable
fun TagChipPicker(
    tags: List<String>,
    selectedTag: String,
    onTagSelected: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    FlowRow(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(WalletTheme.spacing.small),
        verticalArrangement = Arrangement.spacedBy(WalletTheme.spacing.small)
    ) {
        tags.forEach { tag ->
            val selected = tag == selectedTag
            val containerColor = if (selected) {
                MaterialTheme.colorScheme.primary
            } else {
                MaterialTheme.colorScheme.secondary
            }
            val contentColor = if (selected) {
                MaterialTheme.colorScheme.onPrimary
            } else {
                MaterialTheme.colorScheme.onSurfaceVariant
            }
            Box(
                modifier = Modifier
                    .clip(CircleShape)
                    .background(containerColor)
                    .clickable { onTagSelected(tag) }
                    .padding(horizontal = WalletTheme.spacing.medium, vertical = WalletTheme.spacing.extraSmall + 2.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = tag,
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = if (selected) FontWeight.SemiBold else FontWeight.Medium,
                    color = contentColor
                )
            }
        }
    }
}
