package com.app.examenmoviles.domain.model

/**
 * Domain model representing COVID-19 data for a country
 * This is what the UI layer will use to display data
 */
data class CountryCovid(
    val country: String,
    val region: String,
    val totalCases: Long,
    val newCases: Long,
    val totalDeaths: Long,
    val newDeaths: Long,
    val lastUpdate: String, // Date of the most recent data
)
