package com.babacode.walletexpensetracker.ui.setting

import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import com.babacode.walletexpensetracker.ui.setting.compose.SettingsScreen
import com.babacode.walletexpensetracker.ui.setting.compose.SettingsUiState
import com.babacode.walletexpensetracker.ui.theme.WalletExpenseTheme
import org.junit.Rule
import org.junit.Test

class SettingsScreenTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    private val defaultState = SettingsUiState(
        themeValue = "-1",
        themeDescription = "System default",
        currencyValue = "$",
        notificationsEnabled = true
    )

    @Test
    fun notificationSwitch_toggling_invokesCallbackWithFlippedValue() {
        var toggledTo: Boolean? = null
        composeTestRule.setContent {
            WalletExpenseTheme {
                SettingsScreen(
                    uiState = defaultState,
                    onThemeSelected = {},
                    onCurrencySelected = {},
                    onNotificationToggle = { toggledTo = it },
                    onPrivacyPolicyClick = {},
                    onContactSupportClick = {},
                    onReportBugClick = {}
                )
            }
        }

        composeTestRule.onNodeWithText("Notification For New Day").performClick()

        assert(toggledTo == false)
    }

    @Test
    fun themeDialog_opensAndSelectingOption_invokesCallbackAndDismisses() {
        var selectedTheme: String? = null
        composeTestRule.setContent {
            WalletExpenseTheme {
                SettingsScreen(
                    uiState = defaultState,
                    onThemeSelected = { selectedTheme = it },
                    onCurrencySelected = {},
                    onNotificationToggle = {},
                    onPrivacyPolicyClick = {},
                    onContactSupportClick = {},
                    onReportBugClick = {}
                )
            }
        }

        composeTestRule.onNodeWithText("Theme").performClick()
        composeTestRule.onNodeWithText("Dark").assertExists()
        composeTestRule.onNodeWithText("Dark").performClick()

        assert(selectedTheme == "2")
        composeTestRule.onNodeWithText("Dark").assertDoesNotExist()
    }
}
