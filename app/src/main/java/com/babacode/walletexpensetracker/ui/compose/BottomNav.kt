package com.babacode.walletexpensetracker.ui.compose

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation3.runtime.NavKey
import com.babacode.walletexpensetracker.R
import com.babacode.walletexpensetracker.ui.navigation.Budgets
import com.babacode.walletexpensetracker.ui.navigation.Home
import com.babacode.walletexpensetracker.ui.navigation.Insights
import com.babacode.walletexpensetracker.ui.navigation.TransactionTypeDetail
import com.babacode.walletexpensetracker.ui.theme.WalletExpenseTheme
import com.babacode.walletexpensetracker.ui.theme.WalletTheme

// Bottom tab bar + floating add-transaction button, matching the reference
// design's BottomNav (refrence/src/components/app/BottomNav.tsx): 4 destinations
// plus a circular FAB that floats just above the bar on the trailing edge.
@Composable
fun BottomNav(
    currentRoute: NavKey?,
    onNavigate: (NavKey) -> Unit,
    onAddClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier.fillMaxWidth()) {
        Box(
            modifier = Modifier.fillMaxWidth(),
            contentAlignment = Alignment.CenterEnd
        ) {
            FloatingActionButton(
                onClick = onAddClick,
                modifier = Modifier.padding(end = WalletTheme.spacing.large, bottom = WalletTheme.spacing.small),
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

        NavigationBar {
            NavigationBarItem(
                selected = currentRoute is Home,
                onClick = { onNavigate(Home) },
                icon = { Icon(painterResource(R.drawable.home_vector), contentDescription = null) },
                label = { Text(stringResource(R.string.nav_home)) }
            )
            NavigationBarItem(
                selected = currentRoute is TransactionTypeDetail,
                onClick = { onNavigate(TransactionTypeDetail(null)) },
                icon = { Icon(painterResource(R.drawable.bar_chart_vector), contentDescription = null) },
                label = { Text(stringResource(R.string.nav_detail)) }
            )
            NavigationBarItem(
                selected = currentRoute is Insights,
                onClick = { onNavigate(Insights) },
                icon = { Icon(painterResource(R.drawable.pie_chart_vector), contentDescription = null) },
                label = { Text(stringResource(R.string.nav_insights)) }
            )
            NavigationBarItem(
                selected = currentRoute is Budgets,
                onClick = { onNavigate(Budgets) },
                icon = { Icon(painterResource(R.drawable.wallet_vector), contentDescription = null) },
                label = { Text(stringResource(R.string.nav_budgets)) }
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
