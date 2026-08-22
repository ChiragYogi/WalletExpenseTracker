package com.babacode.walletexpensetracker.ui.addedit.compose

data class TransactionAddEditUiState(
    val transactionId: Int = 0,
    val type: String = "",
    val amount: String = "",
    val note: String = "",
    val date: String = "",
    val tag: String = "",
    val paymentMode: String = "",
) {
    companion object {
        val Default = TransactionAddEditUiState()
    }
}
