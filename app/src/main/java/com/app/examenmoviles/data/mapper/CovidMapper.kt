package com.app.examenmoviles.data.mapper

import com.app.examenmoviles.data.remote.dto.CovidCountryDto
import com.app.examenmoviles.domain.model.CountryCovid

/**
 * Mapper to convert from data layer DTOs to domain models
 */
object CovidMapper {
    /**
     * Converts a CovidCountryDto to a CountryCovid domain model
     * Extracts the most recent date's data from the cases/deaths maps
     */
    fun CovidCountryDto.toDomain(): CountryCovid {
        // Get the most recent date from cases or deaths map
        val latestCaseDate = cases?.keys?.maxOrNull()
        val latestDeathDate = deaths?.keys?.maxOrNull()
        val lastUpdate = latestCaseDate ?: latestDeathDate ?: "Unknown"

        // Extract case data for the latest date
        val latestCaseData = latestCaseDate?.let { cases?.get(it) }
        val totalCases = latestCaseData?.total ?: 0L
        val newCases = latestCaseData?.new ?: 0L

        // Extract death data for the latest date
        val latestDeathData = latestDeathDate?.let { deaths?.get(it) }
        val totalDeaths = latestDeathData?.total ?: 0L
        val newDeaths = latestDeathData?.new ?: 0L

        return CountryCovid(
            country = country,
            region = region ?: "",
            totalCases = totalCases,
            newCases = newCases,
            totalDeaths = totalDeaths,
            newDeaths = newDeaths,
            lastUpdate = lastUpdate,
        )
    }

    /**
     * Merges cases and deaths data from two API responses into a single CountryCovid
     * @param casesDto DTO with cases data
     * @param deathsDto DTO with deaths data
     */
    fun mergeCasesAndDeaths(
        casesDto: CovidCountryDto?,
        deathsDto: CovidCountryDto?,
    ): CountryCovid? {
        // Need at least one of them
        if (casesDto == null && deathsDto == null) return null

        val country = casesDto?.country ?: deathsDto?.country ?: return null
        val region = casesDto?.region ?: deathsDto?.region ?: ""

        // Extract cases data
        val latestCaseDate = casesDto?.cases?.keys?.maxOrNull()
        val latestCaseData = latestCaseDate?.let { casesDto.cases?.get(it) }
        val totalCases = latestCaseData?.total ?: 0L
        val newCases = latestCaseData?.new ?: 0L

        // Extract deaths data
        val latestDeathDate = deathsDto?.deaths?.keys?.maxOrNull()
        val latestDeathData = latestDeathDate?.let { deathsDto.deaths?.get(it) }
        val totalDeaths = latestDeathData?.total ?: 0L
        val newDeaths = latestDeathData?.new ?: 0L

        // Use the most recent date from either cases or deaths
        val lastUpdate = latestCaseDate ?: latestDeathDate ?: "Unknown"

        return CountryCovid(
            country = country,
            region = region,
            totalCases = totalCases,
            newCases = newCases,
            totalDeaths = totalDeaths,
            newDeaths = newDeaths,
            lastUpdate = lastUpdate,
        )
    }

    /**
     * Converts a list of CovidCountryDto to a list of CountryCovid
     */
    fun List<CovidCountryDto>.toDomainList(): List<CountryCovid> = map { it.toDomain() }
}
