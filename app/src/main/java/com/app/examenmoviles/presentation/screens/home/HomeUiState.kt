package com.app.examenmoviles.presentation.screens.home

import com.app.examenmoviles.domain.model.CountryCovid

/**
 * UI State for the Home Screen
 * Manages state for both tabs: Country List and Search
 */
data class HomeUiState(
    // Country List Tab state
    val topCountries: List<CountryCovid> = emptyList(),
    val isLoadingTopCountries: Boolean = false,
    val topCountriesError: String? = null,

    // Search Tab state
    val searchQuery: String = "",
    val searchResult: CountryCovid? = null,
    val isSearching: Boolean = false,
    val searchError: String? = null,

    // Current selected tab (0 = Country List, 1 = Search)
    val selectedTabIndex: Int = 0,

    // Pull-to-refresh state
    val isRefreshing: Boolean = false
)