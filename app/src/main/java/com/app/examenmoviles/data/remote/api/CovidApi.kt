package com.app.examenmoviles.data.remote.api

import com.app.examenmoviles.data.remote.dto.CovidCountryDto
import retrofit2.http.GET
import retrofit2.http.Query

interface CovidApi {

    /**
     * Fetches COVID-19 data for a specific country
     * @param country Country name (e.g., "United States", "Mexico", "Spain")
     * @return List of COVID-19 data for the specified country
     */
    @GET("v1/covid19")
    suspend fun getCountryCovidData(
        @Query("country") country: String
    ): List<CovidCountryDto>

    /**
     * Fetches COVID-19 data for multiple countries
     * Note: The API doesn't support fetching multiple countries at once,
     * so this will need to be called multiple times for different countries
     */
    @GET("v1/covid19")
    suspend fun getAllCountriesData(): List<CovidCountryDto>
}