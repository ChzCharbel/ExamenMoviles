package com.app.examenmoviles.data.remote.api

import com.app.examenmoviles.data.remote.dto.CovidCountryDto
import retrofit2.http.GET
import retrofit2.http.Query

interface CovidApi {
    /**
     * Fetches COVID-19 data for a specific country
     * @param country Country name (e.g., "United States", "Mexico", "Spain")
     * @param type Type of data to fetch: "cases" or "deaths" (default: "cases")
     * @param date Optional date in YYYY-MM-DD format (e.g., "2023-01-15")
     * @return List of COVID-19 data for the specified country
     */
    @GET("v1/covid19")
    suspend fun getCountryCovidData(
        @Query("country") country: String,
        @Query("type") type: String = "cases",
        @Query("date") date: String? = null,
    ): List<CovidCountryDto>
}
