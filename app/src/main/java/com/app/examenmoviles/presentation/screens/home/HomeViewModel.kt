package com.app.examenmoviles.presentation.screens.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.app.examenmoviles.domain.usecase.GetCountryCovidData
import com.app.examenmoviles.domain.usecase.GetCountryCovidList
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * ViewModel for the Home Screen
 * Manages state for both Country List and Search tabs
 */
@HiltViewModel
class HomeViewModel @Inject constructor(
    private val getCountryCovidList: GetCountryCovidList,
    private val getCountryCovidData: GetCountryCovidData
) : ViewModel() {

    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    private var searchJob: Job? = null

    init {
        // Load top countries when ViewModel is created
        loadTopCountries()
    }

    /**
     * Loads the top 10 countries from the repository
     * Uses cache if available
     */
    fun loadTopCountries() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoadingTopCountries = true, topCountriesError = null) }

            getCountryCovidList(forceRefresh = false)
                .onSuccess { countries ->
                    _uiState.update {
                        it.copy(
                            topCountries = countries,
                            isLoadingTopCountries = false,
                            topCountriesError = null
                        )
                    }
                }
                .onFailure { error ->
                    _uiState.update {
                        it.copy(
                            isLoadingTopCountries = false,
                            topCountriesError = error.message ?: "Failed to load countries"
                        )
                    }
                }
        }
    }

    /**
     * Refreshes the top countries list from the API (bypasses cache)
     * Used for pull-to-refresh
     */
    fun refreshTopCountries() {
        viewModelScope.launch {
            _uiState.update { it.copy(isRefreshing = true, topCountriesError = null) }

            getCountryCovidList(forceRefresh = true)
                .onSuccess { countries ->
                    _uiState.update {
                        it.copy(
                            topCountries = countries,
                            isRefreshing = false,
                            topCountriesError = null
                        )
                    }
                }
                .onFailure { error ->
                    _uiState.update {
                        it.copy(
                            isRefreshing = false,
                            topCountriesError = error.message ?: "Failed to refresh countries"
                        )
                    }
                }
        }
    }

    /**
     * Updates the search query
     * Debounces the search to avoid excessive API calls
     */
    fun onSearchQueryChanged(query: String) {
        _uiState.update { it.copy(searchQuery = query) }

        // Cancel previous search job
        searchJob?.cancel()

        if (query.isBlank()) {
            // Clear search results if query is empty
            _uiState.update {
                it.copy(
                    searchResult = null,
                    searchError = null,
                    isSearching = false
                )
            }
            return
        }

        // Debounce search for 500ms
        searchJob = viewModelScope.launch {
            delay(500)
            searchCountry(query)
        }
    }

    /**
     * Searches for a specific country
     */
    private fun searchCountry(countryName: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(isSearching = true, searchError = null) }

            getCountryCovidData(countryName)
                .onSuccess { country ->
                    _uiState.update {
                        it.copy(
                            searchResult = country,
                            isSearching = false,
                            searchError = null
                        )
                    }
                }
                .onFailure { error ->
                    _uiState.update {
                        it.copy(
                            searchResult = null,
                            isSearching = false,
                            searchError = error.message ?: "Country not found"
                        )
                    }
                }
        }
    }

    /**
     * Updates the selected tab index
     */
    fun onTabSelected(index: Int) {
        _uiState.update { it.copy(selectedTabIndex = index) }
    }

    /**
     * Clears the search query and results
     */
    fun clearSearch() {
        _uiState.update {
            it.copy(
                searchQuery = "",
                searchResult = null,
                searchError = null,
                isSearching = false
            )
        }
        searchJob?.cancel()
    }
}