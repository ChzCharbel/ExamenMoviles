package com.app.examenmoviles.data.remote.dto

import com.google.gson.annotations.SerializedName

/**
 * DTO for COVID-19 API response
 * The API returns data in the format:
 * {
 *   "country": "United States",
 *   "region": "California",
 *   "cases": {
 *     "2023-01-15": {
 *       "total": 1000000,
 *       "new": 5000
 *     }
 *   }
 * }
 */
data class CovidCountryDto(
    @SerializedName("country")
    val country: String,
    @SerializedName("region")
    val region: String?,
    @SerializedName("cases")
    val cases: Map<String, CaseData>?,
    @SerializedName("deaths")
    val deaths: Map<String, CaseData>?,
)

/**
 * Represents the case/death data for a specific date
 */
data class CaseData(
    @SerializedName("total")
    val total: Long,
    @SerializedName("new")
    val new: Long,
)
