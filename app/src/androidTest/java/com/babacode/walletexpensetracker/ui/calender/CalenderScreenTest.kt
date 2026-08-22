package com.babacode.walletexpensetracker.ui.calender

import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import com.babacode.walletexpensetracker.data.model.PaymentMode
import com.babacode.walletexpensetracker.data.model.Transaction
import com.babacode.walletexpensetracker.data.model.TransactionType
import com.babacode.walletexpensetracker.ui.calender.compose.CalenderScreen
import com.babacode.walletexpensetracker.ui.theme.WalletExpenseTheme
import com.babacode.walletexpensetracker.utiles.Extra
import org.junit.Rule
import org.junit.Test
import java.time.LocalDate

class CalenderScreenTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    private val today: LocalDate = LocalDate.now()

    private val sampleTransaction = Transaction(
        note = "Groceries",
        date = Extra.convertLocalDateToLong(today),
        transactionType = TransactionType.EXPENSE,
        amount = 450.0,
        tag = "Food",
        paymentType = PaymentMode.CASH,
        id = 1
    )

    private fun setScreen(
        transactions: List<Transaction> = emptyList(),
        onTransactionClick: (Transaction) -> Unit = {}
    ) {
        composeTestRule.setContent {
            WalletExpenseTheme {
                CalenderScreen(
                    visibleMonth = today.withDayOfMonth(1),
                    selectedDate = today,
                    spendByDay = emptyMap(),
                    currencyCode = "$",
                    transactions = transactions,
                    onPreviousMonth = {},
                    onNextMonth = {},
                    onDaySelected = {},
                    onTransactionClick = onTransactionClick,
                    onLongPress = {}
                )
            }
        }
    }

    @Test
    fun emptyState_showsNoTransactionMessage() {
        setScreen(transactions = emptyList())

        composeTestRule.onNodeWithText("No transactions on this day.").assertExists()
    }

    @Test
    fun withTransactions_showsRow() {
        setScreen(transactions = listOf(sampleTransaction))

        composeTestRule.onNodeWithText("Groceries").assertExists()
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
