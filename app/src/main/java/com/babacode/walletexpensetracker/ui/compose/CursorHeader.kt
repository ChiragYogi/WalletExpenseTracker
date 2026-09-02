package com.babacode.walletexpensetracker.ui.compose

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import com.babacode.walletexpensetracker.R

// Prev/next cursor navigator with a centered display-font label, matching the
// reference design's period/month navigators (refrence/src/routes/detail.tsx,
// refrence/src/routes/calendar.tsx) — shared by Detail's period cursor and
// Calendar's month cursor.
@Composable
fun CursorHeader(
    label: String,
    onPrevious: () -> Unit,
    onNext: () -> Unit,
    modifier: Modifier = Modifier,
    previousDescription: String = stringResource(R.string.previous_period),
    nextDescription: String = stringResource(R.string.next_period),
    previousIcon: Painter = painterResource(R.drawable.previous_vector),
    nextIcon: Painter = painterResource(R.drawable.next_vector)
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        IconButton(onClick = onPrevious) {
            Icon(
                painter = previousIcon,
                contentDescription = previousDescription,
                tint = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
        Text(
            text = label,
            style = MaterialTheme.typography.titleLarge,
            textAlign = TextAlign.Center
        )
        IconButton(onClick = onNext) {
            Icon(
                painter = nextIcon,
                contentDescription = nextDescription,
                tint = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}
