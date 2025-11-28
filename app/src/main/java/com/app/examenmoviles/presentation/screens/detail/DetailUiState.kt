package com.app.examenmoviles.presentation.screens.detail

import com.app.examenmoviles.domain.model.CountryCovid

/**
 * UI State for the Detail Screen
 * Shows detailed COVID-19 information for a specific country
 */
data class DetailUiState(
    val country: CountryCovid? = null,
    val isLoading: Boolean = false,
    val error: String? = null,
    val selectedDate: String? = null, // Selected date in YYYY-MM-DD format
    val showDatePicker: Boolean = false,
)