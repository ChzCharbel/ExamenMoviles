package com.app.examenmoviles.presentation.common.components

import androidx.compose.material3.Button
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

@Suppress("ktlint:standard:function-naming")
/**
 * Date Picker Dialog for selecting COVID-19 data dates
 * Date range: 2020-01-22 to 2023-03-09
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CovidDatePickerDialog(
    onDateSelected: (String) -> Unit,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier,
) {
    // Define the valid date range
    val minDate =
        Calendar
            .getInstance()
            .apply {
                set(2020, 0, 22) // January 22, 2020
            }.timeInMillis

    val maxDate =
        Calendar
            .getInstance()
            .apply {
                set(2023, 2, 9) // March 9, 2023
            }.timeInMillis

    val datePickerState =
        rememberDatePickerState(
            yearRange = 2020..2023,
        )

    DatePickerDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            Button(
                onClick = {
                    datePickerState.selectedDateMillis?.let { millis ->
                        // Validate date is within range
                        if (millis in minDate..maxDate) {
                            val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
                            val selectedDate = dateFormat.format(Date(millis))
                            onDateSelected(selectedDate)
                        }
                    }
                },
                enabled = datePickerState.selectedDateMillis != null,
            ) {
                Text("OK")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        },
        modifier = modifier,
    ) {
        DatePicker(state = datePickerState)
    }
}