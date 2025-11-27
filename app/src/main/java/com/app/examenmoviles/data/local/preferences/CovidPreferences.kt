package com.app.examenmoviles.data.local.preferences

import android.content.Context
import android.content.SharedPreferences
import com.app.examenmoviles.data.local.model.CovidCache
import com.app.examenmoviles.domain.model.CountryCovid
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Manages caching of COVID data using SharedPreferences
 */
@Singleton
class CovidPreferences @Inject constructor(
    @ApplicationContext private val context: Context,
    private val gson: Gson
) {
    private val prefs: SharedPreferences = context.getSharedPreferences(
        CovidConstants.PREFS_NAME,
        Context.MODE_PRIVATE
    )

    /**
     * Saves the top countries list to cache
     */
    fun saveTopCountries(countries: List<CountryCovid>) {
        val cache = CovidCache(data = countries)
        val json = gson.toJson(cache)
        prefs.edit()
            .putString(CovidConstants.KEY_TOP_COUNTRIES_LIST, json)
            .putLong(CovidConstants.KEY_LAST_FETCH_TIME, System.currentTimeMillis())
            .apply()
    }

    /**
     * Retrieves the cached top countries list if valid
     */
    fun getTopCountries(): CovidCache? {
        val json = prefs.getString(CovidConstants.KEY_TOP_COUNTRIES_LIST, null) ?: return null
        return try {
            val type = object : TypeToken<CovidCache>() {}.type
            gson.fromJson<CovidCache>(json, type)
        } catch (e: Exception) {
            null
        }
    }

    /**
     * Saves a specific country's data to cache
     */
    fun saveCountryData(country: String, data: CountryCovid) {
        val cache = CovidCache(data = listOf(data))
        val json = gson.toJson(cache)
        prefs.edit()
            .putString("${CovidConstants.KEY_CACHED_COUNTRIES}_${country.lowercase()}", json)
            .apply()
    }

    /**
     * Retrieves a specific country's cached data if valid
     */
    fun getCountryData(country: String): CovidCache? {
        val json = prefs.getString("${CovidConstants.KEY_CACHED_COUNTRIES}_${country.lowercase()}", null) ?: return null
        return try {
            val type = object : TypeToken<CovidCache>() {}.type
            gson.fromJson<CovidCache>(json, type)
        } catch (e: Exception) {
            null
        }
    }

    /**
     * Clears all cached data
     */
    fun clearCache() {
        prefs.edit().clear().apply()
    }
}