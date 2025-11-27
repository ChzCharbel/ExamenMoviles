package com.app.examenmoviles.data.local.model

import com.app.examenmoviles.domain.model.CountryCovid

/**
 * Cache model for storing COVID data with timestamp
 */
data class CovidCache(
    val data: List<CountryCovid>,
    val timestamp: Long = System.currentTimeMillis(),
) {
    /**
     * Checks if the cache is still valid based on the validity duration
     */
    fun isValid(validityDuration: Long): Boolean {
        val currentTime = System.currentTimeMillis()
        return (currentTime - timestamp) < validityDuration
    }
}
