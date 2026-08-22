package com.babacode.walletexpensetracker.ui.compose

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation3.runtime.NavKey

// Wraps the whole app's routed content with the bottom nav bar, matching the
// reference design's PhoneShell (refrence/src/components/app/PhoneShell.tsx),
// minus its desktop-preview "phone frame" chrome, which has no Android analog.
// contentWindowInsets is zeroed so this only reserves space for the bottom nav's
// own measured height; each screen's own Scaffold/TopAppBar still owns the
// system bar insets exactly as before, avoiding doubled padding.
@Composable
fun AppScaffold(
    showBottomNav: Boolean,
    currentRoute: NavKey?,
    onNavigate: (NavKey) -> Unit,
    onAddClick: () -> Unit,
    modifier: Modifier = Modifier,
    content: @Composable (PaddingValues) -> Unit
) {
    Scaffold(
        modifier = modifier,
        contentWindowInsets = WindowInsets(0, 0, 0, 0),
        bottomBar = {
            if (showBottomNav) {
                BottomNav(
                    currentRoute = currentRoute,
                    onNavigate = onNavigate,
                    onAddClick = onAddClick
                )
            }
        },
        content = content
    )
}
