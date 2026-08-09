package com.babacode.walletexpensetracker.ui.calender

import androidx.compose.material3.rememberDatePickerState
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import com.babacode.walletexpensetracker.data.model.PaymentType
import com.babacode.walletexpensetracker.data.model.Transaction
import com.babacode.walletexpensetracker.data.model.TransactionTag
import com.babacode.walletexpensetracker.data.model.TransactionType
import com.babacode.walletexpensetracker.ui.calender.compose.CalenderScreen
import com.babacode.walletexpensetracker.ui.theme.WalletExpenseTheme
import org.junit.Rule
import org.junit.Test

class CalenderScreenTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    private val sampleTransaction = Transaction(
        note = "Groceries",
        date = 0L,
        transactionType = TransactionType.EXPENSE,
        amount = 450.0,
        tag = TransactionTag.FOOD,
        paymentType = PaymentType.CASH,
        id = 1
    )

    private fun setScreen(
        transactions: List<Transaction> = emptyList(),
        onTransactionClick: (Transaction) -> Unit = {}
    ) {
        composeTestRule.setContent {
            WalletExpenseTheme {
                CalenderScreen(
                    datePickerState = rememberDatePickerState(),
                    currencyCode = "$",
                    transactions = transactions,
                    onTransactionClick = onTransactionClick,
                    onLongPress = {}
                )
            }
        }
    }

    @Test
    fun emptyState_showsNoTransactionMessage() {
        setScreen(transactions = emptyList())

        composeTestRule.onNodeWithText("No Transaction To Show").assertExists()
    }

    @Test
    fun withTransactions_showsRow() {
        setScreen(transactions = listOf(sampleTransaction))

        composeTestRule.onNodeWithText("Groceries").assertExists()
        composeTestRule.onNodeWithText("$ 450.0").assertExists()
    }

    @Test
    fun rowClick_invokesCallback() {
        var clicked: Transaction? = null
        setScreen(
            transactions = listOf(sampleTransaction),
            onTransactionClick = { clicked = it }
        )

        composeTestRule.onNodeWithText("Groceries").performClick()

        assert(clicked == sampleTransaction)
    }
}
