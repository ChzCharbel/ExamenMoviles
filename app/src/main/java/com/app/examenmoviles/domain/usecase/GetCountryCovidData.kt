package com.app.examenmoviles.domain.usecase

import com.app.examenmoviles.domain.model.CountryCovid
import com.app.examenmoviles.domain.repository.CovidRepository
import javax.inject.Inject

/**
 * Use case for fetching COVID-19 data for a specific country
 * This is used by the search tab in the home screen and the detail screen
 */
class GetCountryCovidData
    @Inject
    constructor(
        private val repository: CovidRepository,
    ) {
        /**
         * Fetches COVID data for a specific country
         * @param country Name of the country to fetch data for
         * @param date Optional date in YYYY-MM-DD format (e.g., "2023-01-15")
         * @return Result containing CountryCovid data or an error
         */
        suspend operator fun invoke(
            country: String,
            date: String? = null,
        ): Result<CountryCovid> {
            // Validate input
            if (country.isBlank()) {
                return Result.failure(IllegalArgumentException("Country name cannot be empty"))
            }

            return repository.getCountryData(country.trim(), date)
        }
    }
