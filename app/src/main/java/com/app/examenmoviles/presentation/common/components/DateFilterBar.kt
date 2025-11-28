package com.app.examenmoviles.presentation.common.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Suppress("ktlint:standard:function-naming")
/**
 * Date filter bar component
 * Shows the currently selected date and allows clearing or changing it
 */
@Composable
fun DateFilterBar(
    selectedDate: String?,
    onDateClick: () -> Unit,
    onClearDate: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier =
            modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        FilterChip(
            selected = selectedDate != null,
            onClick = onDateClick,
            label = {
                Text(
                    text =
                        if (selectedDate != null) {
                            "Date: $selectedDate"
                        } else {
                            "Select Date"
                        },
                )
            },
            leadingIcon = {
                Icon(
                    imageVector = Icons.Default.DateRange,
                    contentDescription = "Calendar",
                )
            },
        )

        if (selectedDate != null) {
            IconButton(onClick = onClearDate) {
                Icon(
                    imageVector = Icons.Default.Clear,
                    contentDescription = "Clear date filter",
                )
            }
        }
    }
}