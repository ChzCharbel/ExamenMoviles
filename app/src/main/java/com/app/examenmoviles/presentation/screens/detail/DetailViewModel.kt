package com.app.examenmoviles.presentation.screens.detail

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.app.examenmoviles.domain.usecase.GetCountryCovidData
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * ViewModel for the Detail Screen
 * Fetches and manages COVID-19 data for a specific country
 */
@HiltViewModel
class DetailViewModel @Inject constructor(
    private val getCountryCovidData: GetCountryCovidData,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val _uiState = MutableStateFlow(DetailUiState())
    val uiState: StateFlow<DetailUiState> = _uiState.asStateFlow()

    private val countryName: String = savedStateHandle.get<String>("countryName") ?: ""

    init {
        // Load country data when ViewModel is created
        loadCountryData()
    }

    /**
     * Loads COVID-19 data for the specified country
     */
    fun loadCountryData() {
        if (countryName.isEmpty()) {
            _uiState.update {
                it.copy(
                    isLoading = false,
                    error = "Country name is required"
                )
            }
            return
        }

        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }

            getCountryCovidData(countryName, date = _uiState.value.selectedDate)
                .onSuccess { country ->
                    _uiState.update {
                        it.copy(
                            country = country,
                            isLoading = false,
                            error = null
                        )
                    }
                }
                .onFailure { error ->
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            error = error.message ?: "Failed to load country data"
                        )
                    }
                }
        }
    }

    /**
     * Shows the date picker dialog
     */
    fun showDatePicker() {
        _uiState.update { it.copy(showDatePicker = true) }
    }

    /**
     * Hides the date picker dialog
     */
    fun hideDatePicker() {
        _uiState.update { it.copy(showDatePicker = false) }
    }

    /**
     * Updates the selected date and refreshes data
     * @param date Date in YYYY-MM-DD format, or null to clear the filter
     */
    fun onDateSelected(date: String?) {
        _uiState.update { it.copy(selectedDate = date, showDatePicker = false) }
        loadCountryData()
    }

    /**
     * Clears the date filter
     */
    fun clearDateFilter() {
        onDateSelected(null)
    }
}