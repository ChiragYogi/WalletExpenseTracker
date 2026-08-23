package com.babacode.walletexpensetracker.ui.addedit.compose

import android.app.DatePickerDialog
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExposedDropdownMenuAnchorType as MenuAnchorType
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import com.babacode.walletexpensetracker.R
import com.babacode.walletexpensetracker.ui.theme.ShapeMedium
import com.babacode.walletexpensetracker.ui.theme.WalletTheme
import java.util.Calendar

// Matches the reference design's shared field style (refrence/src/components/app/TransactionForm.tsx
// `fieldCls`): a bordered, card-colored box with the field's label shown above it rather than as a
// floating M3 label inside the box.
@Composable
fun FieldLabel(text: String, modifier: Modifier = Modifier) {
    Text(
        text = text,
        style = MaterialTheme.typography.labelMedium,
        color = MaterialTheme.colorScheme.onSurfaceVariant,
        modifier = modifier
    )
}

@Composable
fun formFieldColors() = OutlinedTextFieldDefaults.colors(
    focusedContainerColor = MaterialTheme.colorScheme.surface,
    unfocusedContainerColor = MaterialTheme.colorScheme.surface,
    disabledContainerColor = MaterialTheme.colorScheme.surface,
    focusedBorderColor = MaterialTheme.colorScheme.primary,
    unfocusedBorderColor = MaterialTheme.colorScheme.outline,
    disabledBorderColor = MaterialTheme.colorScheme.outline,
    disabledTextColor = MaterialTheme.colorScheme.onSurface,
    disabledPlaceholderColor = MaterialTheme.colorScheme.onSurfaceVariant,
    disabledTrailingIconColor = MaterialTheme.colorScheme.onSurfaceVariant
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DropdownField(
    label: String,
    options: List<String>,
    selected: String,
    modifier: Modifier = Modifier,
    onOptionSelected: (String) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    Column(modifier = modifier.fillMaxWidth()) {
        FieldLabel(label)
        ExposedDropdownMenuBox(
            expanded = expanded,
            onExpandedChange = { expanded = it },
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = WalletTheme.spacing.extraSmall)
        ) {
            OutlinedTextField(
                value = selected,
                onValueChange = {},
                readOnly = true,
                shape = ShapeMedium,
                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                colors = formFieldColors(),
                modifier = Modifier
                    .fillMaxWidth()
                    .menuAnchor(MenuAnchorType.PrimaryNotEditable)
            )
            ExposedDropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false }
            ) {
                options.forEach { option ->
                    DropdownMenuItem(
                        text = { Text(option) },
                        onClick = {
                            onOptionSelected(option)
                            expanded = false
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun DateField(
    label: String,
    value: String,
    dateFormatPattern: String,
    modifier: Modifier = Modifier,
    onDateSelected: (String) -> Unit
) {
    val context = LocalContext.current

    Column(modifier = modifier.fillMaxWidth()) {
        FieldLabel(label)
        // OutlinedTextField is `enabled = false` (so it never intercepts touch for text-editing/IME)
        // with its disabled colors overridden to match the enabled look; the wrapping Box supplies the
        // click, keeping the field's text in the accessibility tree (an overlay Box on top would swallow it).
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = WalletTheme.spacing.extraSmall)
                .clickable {
                    val calendar = Calendar.getInstance()
                    DatePickerDialog(
                        context,
                        { _, year, month, dayOfMonth ->
                            calendar.set(Calendar.YEAR, year)
                            calendar.set(Calendar.MONTH, month)
                            calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth)
                            val sdf = java.text.SimpleDateFormat(dateFormatPattern, java.util.Locale.US)
                            onDateSelected(sdf.format(calendar.time))
                        },
                        calendar.get(Calendar.YEAR),
                        calendar.get(Calendar.MONTH),
                        calendar.get(Calendar.DAY_OF_MONTH)
                    ).show()
                }
        ) {
            OutlinedTextField(
                value = value,
                onValueChange = {},
                enabled = false,
                shape = ShapeMedium,
                trailingIcon = {
                    Icon(painter = painterResource(R.drawable.weekly_calender_vector), contentDescription = null)
                },
                colors = formFieldColors(),
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}
