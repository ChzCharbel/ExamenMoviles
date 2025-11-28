package com.app.examenmoviles.presentation.screens.detail

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.app.examenmoviles.domain.model.CountryCovid
import com.app.examenmoviles.presentation.common.components.CovidDatePickerDialog
import com.app.examenmoviles.presentation.common.components.DateFilterBar
import com.app.examenmoviles.presentation.common.components.ErrorView
import java.text.NumberFormat
import java.util.Locale

/**
 * Detail screen showing comprehensive COVID-19 data for a specific country
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetailScreen(
    countryName: String,
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: DetailViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(countryName) },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors()
            )
        },
        modifier = modifier
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            // Date Filter Bar
            DateFilterBar(
                selectedDate = uiState.selectedDate,
                onDateClick = { viewModel.showDatePicker() },
                onClearDate = { viewModel.clearDateFilter() },
            )

            Box(
                modifier = Modifier.fillMaxSize()
            ) {
                when {
                    // Loading state
                    uiState.isLoading -> {
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(48.dp)
                            )
                        }
                    }

                    // Error state
                    uiState.error != null -> {
                        ErrorView(
                            message = uiState.error!!,
                            onRetry = { viewModel.loadCountryData() }
                        )
                    }

                    // Success state
                    uiState.country != null -> {
                        DetailContent(
                            country = uiState.country!!,
                            selectedDate = uiState.selectedDate,
                            modifier = Modifier.fillMaxSize()
                        )
                    }
                }
            }
        }

        // Date Picker Dialog
        if (uiState.showDatePicker) {
            CovidDatePickerDialog(
                onDateSelected = { date ->
                    viewModel.onDateSelected(date)
                },
                onDismiss = { viewModel.hideDatePicker() },
            )
        }
    }
}

@Composable
private fun DetailContent(
    country: CountryCovid,
    selectedDate: String?,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Country Header
        Text(
            text = country.country,
            style = MaterialTheme.typography.headlineLarge,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurface
        )

        if (country.region.isNotBlank()) {
            Text(
                text = country.region,
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Cases Section
        StatCard(
            title = "Cases",
            color = MaterialTheme.colorScheme.primary
        ) {
            StatRow(
                label = "Total Cases",
                value = country.totalCases.formatNumber(),
                valueColor = MaterialTheme.colorScheme.primary
            )
            StatRow(
                label = "New Cases",
                value = "+${country.newCases.formatNumber()}",
                valueColor = MaterialTheme.colorScheme.error
            )
        }

        // Deaths Section
        StatCard(
            title = "Deaths",
            color = MaterialTheme.colorScheme.error
        ) {
            StatRow(
                label = "Total Deaths",
                value = country.totalDeaths.formatNumber(),
                valueColor = MaterialTheme.colorScheme.error
            )
            StatRow(
                label = "New Deaths",
                value = "+${country.newDeaths.formatNumber()}",
                valueColor = MaterialTheme.colorScheme.error
            )
        }

        // Additional Info
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surfaceVariant
            ),
            shape = RoundedCornerShape(12.dp)
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Date Consulted
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "Date Consulted:",
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = selectedDate ?: "Latest",
                        style = MaterialTheme.typography.bodyLarge,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                }

                // Last Updated (from API data)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "Data Date:",
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = country.lastUpdate,
                        style = MaterialTheme.typography.bodyLarge,
                        fontWeight = FontWeight.Medium,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
            }
        }
    }
}

@Composable
private fun StatCard(
    title: String,
    color: androidx.compose.ui.graphics.Color,
    content: @Composable () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = color
            )
            content()
        }
    }
}

@Composable
private fun StatRow(
    label: String,
    value: String,
    valueColor: androidx.compose.ui.graphics.Color
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Text(
            text = value,
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold,
            color = valueColor
        )
    }
}

/**
 * Extension function to format numbers with commas
 */
private fun Long.formatNumber(): String {
    return NumberFormat.getNumberInstance(Locale.US).format(this)
}