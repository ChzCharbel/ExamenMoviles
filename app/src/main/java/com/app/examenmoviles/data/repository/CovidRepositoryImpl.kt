package com.app.examenmoviles.data.repository

import com.app.examenmoviles.data.local.preferences.CovidConstants
import com.app.examenmoviles.data.local.preferences.CovidPreferences
import com.app.examenmoviles.data.mapper.CovidMapper
import com.app.examenmoviles.data.mapper.CovidMapper.toDomain
import com.app.examenmoviles.data.remote.api.CovidApi
import com.app.examenmoviles.domain.model.CountryCovid
import com.app.examenmoviles.domain.repository.CovidRepository
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Implementation of CovidRepository with caching logic
 * Handles fetching from API and caching using SharedPreferences
 */
@Singleton
class CovidRepositoryImpl
    @Inject
    constructor(
        private val api: CovidApi,
        private val preferences: CovidPreferences,
    ) : CovidRepository {
        /**
         * Fetches top countries data
         * Strategy: Check cache first, if invalid or missing, fetch from API
         * @param date Optional date in YYYY-MM-DD format
         */
        override suspend fun getTopCountries(date: String?): Result<List<CountryCovid>> {
            return try {
                // If date is specified, always fetch from API (don't use cache)
                if (date != null) {
                    return fetchTopCountriesFromApi(date)
                }

                // Check cache first
                val cachedData = preferences.getTopCountries()
                if (cachedData != null && cachedData.isValid(CovidConstants.CACHE_VALIDITY_DURATION)) {
                    return Result.success(cachedData.data)
                }

                // Cache is invalid or missing, fetch from API
                fetchTopCountriesFromApi(null)
            } catch (e: Exception) {
                Result.failure(e)
            }
        }

        /**
         * Fetches a specific country's data
         * Strategy: Check cache first, if invalid or missing, fetch from API
         * Fetches both cases and deaths data separately and merges them
         * @param date Optional date in YYYY-MM-DD format
         */
        override suspend fun getCountryData(
            country: String,
            date: String?,
        ): Result<CountryCovid> {
            return try {
                // If date is specified, always fetch from API (don't use cache)
                if (date == null) {
                    // Check cache first
                    val cachedData = preferences.getCountryData(country)
                    if (cachedData != null && cachedData.isValid(CovidConstants.CACHE_VALIDITY_DURATION)) {
                        return Result.success(cachedData.data.first())
                    }
                }

                // Cache is invalid or missing, fetch both cases and deaths from API
                val casesResponse =
                    try {
                        api.getCountryCovidData(country, type = "cases", date = date).firstOrNull()
                    } catch (e: Exception) {
                        null
                    }

                val deathsResponse =
                    try {
                        api.getCountryCovidData(country, type = "deaths", date = date).firstOrNull()
                    } catch (e: Exception) {
                        null
                    }

                // Merge the data
                val domainData =
                    CovidMapper.mergeCasesAndDeaths(casesResponse, deathsResponse, date)
                        ?: return Result.failure(Exception("No data found for country: $country"))

                // Save to cache only if no date specified
                if (date == null) {
                    preferences.saveCountryData(country, domainData)
                }

                Result.success(domainData)
            } catch (e: Exception) {
                Result.failure(e)
            }
        }

        /**
         * Forces a refresh of top countries data from API
         * Bypasses cache completely
         * @param date Optional date in YYYY-MM-DD format
         */
        override suspend fun refreshTopCountries(date: String?): Result<List<CountryCovid>> =
            fetchTopCountriesFromApi(date)

        /**
         * Clears all cached data
         */
        override fun clearCache() {
            preferences.clearCache()
        }

        /**
         * Private helper to fetch top countries from API
         * Makes parallel API calls for better performance
         * @param date Optional date in YYYY-MM-DD format
         */
        private suspend fun fetchTopCountriesFromApi(date: String?): Result<List<CountryCovid>> =
            try {
                // Use coroutineScope to make parallel API calls for top countries
                val countriesData =
                    coroutineScope {
                        CovidConstants.DEFAULT_TOP_COUNTRIES
                            .map { countryName ->
                                async {
                                    try {
                                        // Fetch both cases and deaths data
                                        val casesResponse =
                                            try {
                                                api.getCountryCovidData(countryName, type = "cases", date = date).firstOrNull()
                                            } catch (e: Exception) {
                                                null
                                            }

                                        val deathsResponse =
                                            try {
                                                api.getCountryCovidData(countryName, type = "deaths", date = date).firstOrNull()
                                            } catch (e: Exception) {
                                                null
                                            }

                                        // Merge the data
                                        CovidMapper.mergeCasesAndDeaths(casesResponse, deathsResponse, date)
                                    } catch (e: Exception) {
                                        null // Skip countries that fail
                                    }
                                }
                            }.awaitAll()
                            .filterNotNull()
                    }

                // Take only the first 10 valid results
                val topCountries = countriesData.take(10)

                // Save to cache only if no date specified
                if (topCountries.isNotEmpty() && date == null) {
                    preferences.saveTopCountries(topCountries)
                }

                Result.success(topCountries)
            } catch (e: Exception) {
                Result.failure(e)
            }
    }
