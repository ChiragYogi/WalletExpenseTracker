package com.babacode.walletexpensetracker.ui.compose

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Text
import androidx.compose.ui.graphics.Color
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.RoundRect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Outline
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathOperation
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import com.babacode.walletexpensetracker.R
import com.babacode.walletexpensetracker.ui.navigation.Budgets
import com.babacode.walletexpensetracker.ui.navigation.Home
import com.babacode.walletexpensetracker.ui.navigation.Insights
import com.babacode.walletexpensetracker.ui.navigation.TabRoute
import com.babacode.walletexpensetracker.ui.navigation.TransactionTypeDetail
import com.babacode.walletexpensetracker.ui.theme.RadiusTwoExtraLarge
import com.babacode.walletexpensetracker.ui.theme.WalletExpenseTheme

// Custom Material2-style "docked, cutout" bottom bar: Material3 dropped both
// BottomAppBar's cutoutShape and Scaffold's isFloatingActionButtonDocked/
// FabPosition.Center, so there's no stock M3 component for a FAB that's
// physically notched into the bar at center. This hand-rolls that look:
// a Shape that subtracts a circle from a top-rounded rect, plus a Box that
// splits the 4 tabs left/right of the notch and overlays the FAB on top.
private val FabDiameter = 56.dp
private val FabCutoutMargin = 8.dp
private val BarHeight = 80.dp

private class CutoutBarShape(
    private val fabDiameter: Dp,
    private val cutoutMargin: Dp,
    private val cornerRadius: Dp
) : Shape {
    override fun createOutline(
        size: Size,
        layoutDirection: LayoutDirection,
        density: Density
    ): Outline {
        val cornerRadiusPx = with(density) { cornerRadius.toPx() }
        val barPath = Path().apply {
            addRoundRect(
                RoundRect(
                    rect = Rect(Offset.Zero, size),
                    topLeft = CornerRadius(cornerRadiusPx),
                    topRight = CornerRadius(cornerRadiusPx),
                    bottomLeft = CornerRadius.Zero,
                    bottomRight = CornerRadius.Zero
                )
            )
        }
        val holeRadiusPx = with(density) { (fabDiameter / 2 + cutoutMargin).toPx() }
        val holePath = Path().apply {
            addOval(Rect(center = Offset(size.width / 2, 0f), radius = holeRadiusPx))
        }
        val outlinePath = Path()
        outlinePath.op(barPath, holePath, PathOperation.Difference)
        return Outline.Generic(outlinePath)
    }
}

// Matches the reference design's tab styling (refrence/src/components/app/BottomNav.tsx):
// selected tabs just turn primary-colored, no M3-style indicator pill behind the icon —
// the default indicator/selected colors, derived from our theme's secondary/surface
// tokens, render as a near-invisible label against the bar's background.
@Composable
private fun navItemColors() = NavigationBarItemDefaults.colors(
    selectedIconColor = MaterialTheme.colorScheme.primary,
    selectedTextColor = MaterialTheme.colorScheme.primary,
    unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
    unselectedTextColor = MaterialTheme.colorScheme.onSurfaceVariant,
    indicatorColor = Color.Transparent
)

@Composable
fun BottomNav(
    currentRoute: TabRoute?,
    onNavigate: (TabRoute) -> Unit,
    onAddClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val density = LocalDensity.current
    val barShape = remember(density) {
        CutoutBarShape(
            fabDiameter = FabDiameter,
            cutoutMargin = FabCutoutMargin,
            cornerRadius = RadiusTwoExtraLarge
        )
    }

    // The Box is sized to exactly the bar's own height (not the FAB's protrusion above
    // it), so Scaffold reserves only that much space for content — otherwise content
    // stops short by an extra FabDiameter/2, leaving a band of plain background color
    // between the last content and the bar's rounded top edge. The FAB instead pokes
    // above the Box via a negative offset, which shifts it in the placement phase
    // without inflating the Box's measured size.
    Box(
        modifier = modifier
            .fillMaxWidth()
            .navigationBarsPadding()
            .height(BarHeight)
    ) {
        Row(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .height(BarHeight)
                .shadow(elevation = 8.dp, shape = barShape, clip = false)
                .clip(barShape)
                .background(MaterialTheme.colorScheme.surface, barShape),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(modifier = Modifier.weight(1f), horizontalArrangement = Arrangement.SpaceEvenly) {
                NavigationBarItem(
                    selected = currentRoute is Home,
                    onClick = { onNavigate(Home) },
                    icon = { Icon(painterResource(R.drawable.home_vector), contentDescription = null) },
                    label = { Text(stringResource(R.string.nav_home)) },
                    colors = navItemColors()
                )
                NavigationBarItem(
                    selected = currentRoute is TransactionTypeDetail,
                    onClick = { onNavigate(TransactionTypeDetail(null)) },
                    icon = { Icon(painterResource(R.drawable.bar_chart_vector), contentDescription = null) },
                    label = { Text(stringResource(R.string.nav_detail)) },
                    colors = navItemColors()
                )
            }
            Spacer(modifier = Modifier.width(FabDiameter + FabCutoutMargin * 2))
            Row(modifier = Modifier.weight(1f), horizontalArrangement = Arrangement.SpaceEvenly) {
                NavigationBarItem(
                    selected = currentRoute is Insights,
                    onClick = { onNavigate(Insights) },
                    icon = { Icon(painterResource(R.drawable.pie_chart_vector), contentDescription = null) },
                    label = { Text(stringResource(R.string.nav_insights)) },
                    colors = navItemColors()
                )
                NavigationBarItem(
                    selected = currentRoute is Budgets,
                    onClick = { onNavigate(Budgets) },
                    icon = { Icon(painterResource(R.drawable.wallet_vector), contentDescription = null) },
                    label = { Text(stringResource(R.string.nav_budgets)) },
                    colors = navItemColors()
                )
            }
        }

        FloatingActionButton(
            onClick = onAddClick,
            modifier = Modifier
                .align(Alignment.TopCenter)
                .offset(y = -(FabDiameter / 2)),
            shape = CircleShape,
            containerColor = MaterialTheme.colorScheme.primary,
            contentColor = MaterialTheme.colorScheme.onPrimary
        ) {
            Icon(
                painter = painterResource(R.drawable.add_transaction_vectore),
                contentDescription = stringResource(R.string.add_transaction_fab_description)
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun BottomNavPreview() {
    WalletExpenseTheme {
        BottomNav(
            currentRoute = Home,
            onNavigate = {},
            onAddClick = {}
        )
    }
}
