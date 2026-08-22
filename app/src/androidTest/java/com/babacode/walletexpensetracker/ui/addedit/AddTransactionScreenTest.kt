package com.babacode.walletexpensetracker.ui.addedit

import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import com.babacode.walletexpensetracker.ui.addedit.compose.AddTransactionScreen
import com.babacode.walletexpensetracker.ui.addedit.compose.TransactionAddEditUiState
import com.babacode.walletexpensetracker.ui.theme.WalletExpenseTheme
import org.junit.Rule
import org.junit.Test

class AddTransactionScreenTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    private fun setScreen(
        type: String = "",
        onTypeSelected: (String) -> Unit = {},
        onSaveClick: () -> Unit = {}
    ) {
        composeTestRule.setContent {
            WalletExpenseTheme {
                AddTransactionScreen(
                    currencyCode = "$",
                    dateFormatPattern = "dd MMM, yyyy",
                    uiState = TransactionAddEditUiState(
                        type = type,
                        amount = "",
                        note = "",
                        date = "08 Aug, 2026",
                        tag = "",
                        paymentMode = ""
                    ),
                    onTypeSelected = onTypeSelected,
                    onAmountChange = {},
                    onNoteChange = {},
                    onDateSelected = {},
                    onTagSelected = {},
                    onPaymentModeSelected = {},
                    onSaveClick = onSaveClick
                )
            }
        }
    }

    @Test
    fun typeToggle_selectingOption_invokesCallback() {
        var selectedType: String? = null
        setScreen(onTypeSelected = { selectedType = it })

        composeTestRule.onNodeWithText("Expense").performClick()

        assert(selectedType == "Expense")
    }

    @Test
    fun dateField_showsInitialDefaultDate() {
        setScreen()

        composeTestRule.onNodeWithText("08 Aug, 2026").assertExists()
    }

    @Test
    fun saveButton_click_invokesSaveCallback() {
        var saved = false
        setScreen(onSaveClick = { saved = true })

        composeTestRule.onNodeWithText("Save transaction").performClick()

        assert(saved)
    }
}
