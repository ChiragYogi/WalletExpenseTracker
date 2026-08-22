package com.babacode.walletexpensetracker.ui.compose

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.FabPosition
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.navigation3.runtime.NavKey
import com.babacode.walletexpensetracker.R

// The 300ms here matches MainActivity's NavDisplay transitionSpec, so the bottom
// nav's reported height (and therefore the outer Scaffold's innerPadding) only
// actually changes once this fade finishes — by which point the outgoing screen
// has finished its own 300ms slide-out and is off-screen, instead of visibly
// reflowing into the freed space while it's still on screen. Fade-only (no
// shrinkVertically/expandVertically) is deliberate: a size-animating transition
// would clip BottomNav's FAB, which pokes above its own bounds via a negative
// offset (see BottomNav.kt).
private const val BottomNavVisibilityAnimationMs = 300

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
            AnimatedVisibility(
                visible = showBottomNav,
                enter = fadeIn(tween(BottomNavVisibilityAnimationMs)),
                exit = fadeOut(tween(BottomNavVisibilityAnimationMs))
            ) {
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
