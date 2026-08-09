package com.babacode.walletexpensetracker.ui.home.compose

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import com.babacode.walletexpensetracker.R
import com.babacode.walletexpensetracker.ui.theme.WalletExpenseTheme

@Composable
fun DeleteTransactionDialog(
    onDismissRequest: () -> Unit,
    onCancelClick: () -> Unit,
    onConfirmClick: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismissRequest,
        icon = {
            Icon(
                painter = painterResource(R.drawable.delete_vector),
                contentDescription = null
            )
        },
        title = { Text(stringResource(R.string.delete_this_transaction)) },
        text = { Text(stringResource(R.string.delete_this_transaction_msg)) },
        confirmButton = {
            TextButton(onClick = onConfirmClick) {
                Text(stringResource(R.string.yes_button))
            }
        },
        dismissButton = {
            TextButton(onClick = onCancelClick) {
                Text(stringResource(R.string.cancle_button))
            }
        }
    )
}

@Preview(showBackground = true)
@Composable
private fun DeleteTransactionDialogPreview() {
    WalletExpenseTheme {
        DeleteTransactionDialog(
            onDismissRequest = {},
            onCancelClick = {},
            onConfirmClick = {}
        )
    }
}
