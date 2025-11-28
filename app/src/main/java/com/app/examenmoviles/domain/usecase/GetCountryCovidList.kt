package com.app.examenmoviles.domain.usecase

import com.app.examenmoviles.domain.model.CountryCovid
import com.app.examenmoviles.domain.repository.CovidRepository
import javax.inject.Inject

/**
 * Use case for fetching the list of top countries with COVID-19 data
 * This is used by the first tab in the home screen to display the top 10 countries
 */
class GetCountryCovidList
    @Inject
    constructor(
        private val repository: CovidRepository,
    ) {
        /**
         * Fetches the top countries list with COVID data
         * @param forceRefresh If true, bypasses cache and fetches fresh data from API
         * @param date Optional date in YYYY-MM-DD format (e.g., "2023-01-15")
         * @return Result containing list of CountryCovid or an error
         */
        suspend operator fun invoke(
            forceRefresh: Boolean = false,
            date: String? = null,
        ): Result<List<CountryCovid>> =
            if (forceRefresh) {
                repository.refreshTopCountries(date)
            } else {
                repository.getTopCountries(date)
            }
    }
