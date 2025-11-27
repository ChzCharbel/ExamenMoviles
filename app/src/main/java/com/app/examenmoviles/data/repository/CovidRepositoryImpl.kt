package com.app.examenmoviles.data.repository

import com.app.examenmoviles.data.local.preferences.CovidConstants
import com.app.examenmoviles.data.local.preferences.CovidPreferences
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
         */
        override suspend fun getTopCountries(): Result<List<CountryCovid>> {
            return try {
                // Check cache first
                val cachedData = preferences.getTopCountries()
                if (cachedData != null && cachedData.isValid(CovidConstants.CACHE_VALIDITY_DURATION)) {
                    return Result.success(cachedData.data)
                }

                // Cache is invalid or missing, fetch from API
                fetchTopCountriesFromApi()
            } catch (e: Exception) {
                Result.failure(e)
            }
        }

        /**
         * Fetches a specific country's data
         * Strategy: Check cache first, if invalid or missing, fetch from API
         */
        override suspend fun getCountryData(country: String): Result<CountryCovid> {
            return try {
                // Check cache first
                val cachedData = preferences.getCountryData(country)
                if (cachedData != null && cachedData.isValid(CovidConstants.CACHE_VALIDITY_DURATION)) {
                    return Result.success(cachedData.data.first())
                }

                // Cache is invalid or missing, fetch from API
                val response = api.getCountryCovidData(country)

                if (response.isEmpty()) {
                    return Result.failure(Exception("No data found for country: $country"))
                }

                // Convert to domain model
                val domainData = response.first().toDomain()

                // Save to cache
                preferences.saveCountryData(country, domainData)

                Result.success(domainData)
            } catch (e: Exception) {
                Result.failure(e)
            }
        }

        /**
         * Forces a refresh of top countries data from API
         * Bypasses cache completely
         */
        override suspend fun refreshTopCountries(): Result<List<CountryCovid>> = fetchTopCountriesFromApi()

        /**
         * Clears all cached data
         */
        override fun clearCache() {
            preferences.clearCache()
        }

        /**
         * Private helper to fetch top countries from API
         * Makes parallel API calls for better performance
         */
        private suspend fun fetchTopCountriesFromApi(): Result<List<CountryCovid>> =
            try {
                // Use coroutineScope to make parallel API calls for top countries
                val countriesData =
                    coroutineScope {
                        CovidConstants.DEFAULT_TOP_COUNTRIES
                            .map { countryName ->
                                async {
                                    try {
                                        val response = api.getCountryCovidData(countryName)
                                        if (response.isNotEmpty()) {
                                            response.first().toDomain()
                                        } else {
                                            null
                                        }
                                    } catch (e: Exception) {
                                        null // Skip countries that fail
                                    }
                                }
                            }.awaitAll()
                            .filterNotNull()
                    }

                // Take only the first 10 valid results
                val topCountries = countriesData.take(10)

                // Save to cache
                if (topCountries.isNotEmpty()) {
                    preferences.saveTopCountries(topCountries)
                }

                Result.success(topCountries)
            } catch (e: Exception) {
                Result.failure(e)
            }
    }
