package com.app.examenmoviles.data.local.preferences

/**
 * Constants used for caching and preferences
 */
object CovidConstants {
    // SharedPreferences name
    const val PREFS_NAME = "covid_prefs"

    // Cache validity duration (1 hour in milliseconds)
    const val CACHE_VALIDITY_DURATION = 60 * 60 * 1000L // 1 hour

    // Preference keys
    const val KEY_LAST_FETCH_TIME = "last_fetch_time"
    const val KEY_CACHED_COUNTRIES = "cached_countries"
    const val KEY_TOP_COUNTRIES_LIST = "top_countries_list"

    // Default top 10 countries to fetch
    val DEFAULT_TOP_COUNTRIES = listOf(
        "United States",
        "India",
        "Brazil",
        "France",
        "Germany",
        "United Kingdom",
        "Italy",
        "Spain",
        "Canada",
        "Mexico"
    )
}