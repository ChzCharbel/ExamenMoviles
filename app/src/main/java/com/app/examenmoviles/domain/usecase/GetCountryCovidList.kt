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
         * @return Result containing list of CountryCovid or an error
         */
        suspend operator fun invoke(forceRefresh: Boolean = false): Result<List<CountryCovid>> =
            if (forceRefresh) {
                repository.refreshTopCountries()
            } else {
                repository.getTopCountries()
            }
    }
