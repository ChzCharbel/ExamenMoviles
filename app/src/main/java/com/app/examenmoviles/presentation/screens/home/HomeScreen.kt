package com.app.examenmoviles.presentation.screens.home

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.app.examenmoviles.domain.model.CountryCovid
import com.app.examenmoviles.presentation.common.components.CovidDatePickerDialog
import com.app.examenmoviles.presentation.common.components.DateFilterBar
import com.app.examenmoviles.presentation.screens.home.components.CountryListTab
import com.app.examenmoviles.presentation.screens.home.components.SearchTab

@Suppress("ktlint:standard:function-naming")
/**
 * Home screen with two tabs: Country List and Search
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    onClick: (String) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: HomeViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("COVID-19 Tracker") },
                colors = TopAppBarDefaults.topAppBarColors(),
            )
        },
        modifier = modifier,
    ) { paddingValues ->
        Column(
            modifier =
                Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
        ) {
            // Date Filter Bar
            DateFilterBar(
                selectedDate = uiState.selectedDate,
                onDateClick = { viewModel.showDatePicker() },
                onClearDate = { viewModel.clearDateFilter() },
            )

            // Tab Row
            TabRow(
                selectedTabIndex = uiState.selectedTabIndex,
                modifier = Modifier.fillMaxWidth(),
            ) {
                HomeTabs.entries.forEachIndexed { index, tab ->
                    Tab(
                        selected = uiState.selectedTabIndex == index,
                        onClick = { viewModel.onTabSelected(index) },
                        text = { Text(tab.title) },
                        icon = {
                            Icon(
                                imageVector = tab.icon,
                                contentDescription = tab.title,
                            )
                        },
                    )
                }
            }

            // Tab Content
            when (uiState.selectedTabIndex) {
                0 -> {
                    // Country List Tab
                    CountryListTab(
                        countries = uiState.topCountries,
                        isLoading = uiState.isLoadingTopCountries,
                        isRefreshing = uiState.isRefreshing,
                        error = uiState.topCountriesError,
                        onRefresh = { viewModel.refreshTopCountries() },
                        onRetry = { viewModel.loadTopCountries() },
                        onCountryClick = { country ->
                            onClick(country.country)
                        },
                    )
                }

                1 -> {
                    // Search Tab
                    SearchTab(
                        searchQuery = uiState.searchQuery,
                        searchResult = uiState.searchResult,
                        isSearching = uiState.isSearching,
                        searchError = uiState.searchError,
                        onSearchQueryChange = { query ->
                            viewModel.onSearchQueryChanged(query)
                        },
                        onClearSearch = { viewModel.clearSearch() },
                        onCountryClick = { country ->
                            onClick(country.country)
                        },
                    )
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

/**
 * Enum representing the tabs in the home screen
 */
enum class HomeTabs(
    val title: String,
    val icon: ImageVector,
) {
    COUNTRY_LIST("Countries", Icons.Default.List),
    SEARCH("Search", Icons.Default.Search),
}
