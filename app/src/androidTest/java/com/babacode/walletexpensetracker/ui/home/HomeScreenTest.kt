package com.babacode.walletexpensetracker.ui.home

import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import com.babacode.walletexpensetracker.data.model.PaymentMode
import com.babacode.walletexpensetracker.data.model.Transaction
import com.babacode.walletexpensetracker.data.model.TransactionType
import com.babacode.walletexpensetracker.ui.home.HomeUiState
import com.babacode.walletexpensetracker.ui.home.compose.HomeScreen
import com.babacode.walletexpensetracker.ui.theme.WalletExpenseTheme
import com.babacode.walletexpensetracker.utiles.Extra
import org.junit.Rule
import org.junit.Test

class HomeScreenTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    private val sampleTransaction = Transaction(
        note = "Groceries",
        date = Extra.currentDayDate(),
        transactionType = TransactionType.EXPENSE,
        amount = 450.0,
        tag = "Food",
        paymentType = PaymentMode.CASH,
        id = 1
    )

    private fun setScreen(
        transactions: List<Transaction> = emptyList(),
        onIncomeClick: () -> Unit = {},
        onExpenseClick: () -> Unit = {},
        onTransactionClick: (Transaction) -> Unit = {}
    ) {
        composeTestRule.setContent {
            WalletExpenseTheme {
                HomeScreen(
                    currencyCode = "$",
                    uiState = HomeUiState(
                        hasAnyTransactions = transactions.isNotEmpty(),
                        recentTransactions = transactions
                    ),
                    onIncomeClick = onIncomeClick,
                    onExpenseClick = onExpenseClick,
                    onTransactionClick = onTransactionClick,
                    onLongPress = {}
                )
            }
        }
    }

    @Test
    fun emptyState_showsNoTransactionMessage() {
        setScreen(transactions = emptyList())

        composeTestRule.onNodeWithText("No Transaction Yet!").assertExists()
    }

    @Test
    fun withTransactions_showsRowAndSummaryCards() {
        setScreen(transactions = listOf(sampleTransaction))

        composeTestRule.onNodeWithText("Groceries").assertExists()
        composeTestRule.onNodeWithTag("summary_card_Income").assertExists()
        composeTestRule.onNodeWithTag("summary_card_Expense").assertExists()
    }

    @Test
    fun incomeCardClick_invokesCallback() {
        var clicked = false
        setScreen(
            transactions = listOf(sampleTransaction),
            onIncomeClick = { clicked = true }
        )

        composeTestRule.onNodeWithTag("summary_card_Income").performClick()

        assert(clicked)
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
