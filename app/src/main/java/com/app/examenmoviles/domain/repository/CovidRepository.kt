package com.app.examenmoviles.domain.repository

import com.app.examenmoviles.domain.model.CountryCovid

/**
 * Repository interface for COVID-19 data operations
 * Defines the contract for fetching COVID data
 */
interface CovidRepository {
    /**
     * Fetches COVID data for the top countries
     * Uses cache if available and valid, otherwise fetches from API
     * @param date Optional date in YYYY-MM-DD format (e.g., "2023-01-15")
     * @return Result containing list of COVID data for top countries
     */
    suspend fun getTopCountries(date: String? = null): Result<List<CountryCovid>>

    /**
     * Fetches COVID data for a specific country
     * Uses cache if available and valid, otherwise fetches from API
     * @param country Name of the country to fetch data for
     * @param date Optional date in YYYY-MM-DD format (e.g., "2023-01-15")
     * @return Result containing COVID data for the specified country
     */
    suspend fun getCountryData(
        country: String,
        date: String? = null,
    ): Result<CountryCovid>

    /**
     * Forces a refresh of the top countries data from the API
     * Bypasses cache
     * @param date Optional date in YYYY-MM-DD format (e.g., "2023-01-15")
     * @return Result containing list of COVID data for top countries
     */
    suspend fun refreshTopCountries(date: String? = null): Result<List<CountryCovid>>

    /**
     * Clears all cached data
     */
    fun clearCache()
}
