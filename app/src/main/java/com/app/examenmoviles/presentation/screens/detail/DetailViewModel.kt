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

            getCountryCovidData(countryName)
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
}