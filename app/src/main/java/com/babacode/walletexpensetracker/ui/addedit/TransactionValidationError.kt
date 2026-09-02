package com.babacode.walletexpensetracker.ui.addedit

enum class TransactionValidationError {
    SELECT_TRANSACTION_TYPE,
    ENTER_AMOUNT,
    AMOUNT_TOO_LARGE,
    INVALID_AMOUNT,
    ENTER_NOTE,
    NOTE_TOO_LONG,
    SELECT_TAG,
    SELECT_PAYMENT_MODE,
    INVALID_DATE,
    SAVE_ERROR
}
