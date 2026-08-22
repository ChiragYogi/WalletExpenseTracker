package com.babacode.walletexpensetracker.ui.compose

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.babacode.walletexpensetracker.ui.theme.WalletTheme

// Flex-wrap pill picker, matching the reference design's tag picker
// (refrence/src/components/app/TransactionForm.tsx) — the list of tags shown
// depends on the transaction type (see data/model/TagCatalog.kt).
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
            FilterChip(
                selected = tag == selectedTag,
                onClick = { onTagSelected(tag) },
                label = { Text(tag) }
            )
        }
    }
}
