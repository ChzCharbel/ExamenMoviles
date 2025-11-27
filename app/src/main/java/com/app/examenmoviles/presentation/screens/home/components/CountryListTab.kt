package com.app.examenmoviles.presentation.screens.home.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.pullrefresh.PullRefreshIndicator
import androidx.compose.material.pullrefresh.pullRefresh
import androidx.compose.material.pullrefresh.rememberPullRefreshState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.app.examenmoviles.domain.model.CountryCovid
import com.app.examenmoviles.presentation.common.components.ErrorView
import com.app.examenmoviles.presentation.common.components.LoadingShimmer

/**
 * Tab displaying the list of top 10 countries with COVID data
 * Includes pull-to-refresh functionality
 */
@OptIn(ExperimentalMaterialApi::class)
@Composable
fun CountryListTab(
    countries: List<CountryCovid>,
    isLoading: Boolean,
    isRefreshing: Boolean,
    error: String?,
    onRefresh: () -> Unit,
    onRetry: () -> Unit,
    onCountryClick: (CountryCovid) -> Unit,
    modifier: Modifier = Modifier
) {
    val pullRefreshState = rememberPullRefreshState(
        refreshing = isRefreshing,
        onRefresh = onRefresh
    )

    Box(
        modifier = modifier
            .fillMaxSize()
            .pullRefresh(pullRefreshState)
    ) {
        when {
            // Show loading shimmer on initial load
            isLoading && countries.isEmpty() -> {
                LoadingShimmer()
            }

            // Show error if there's an error and no cached data
            error != null && countries.isEmpty() -> {
                ErrorView(
                    message = error,
                    onRetry = onRetry
                )
            }

            // Show the country list
            countries.isNotEmpty() -> {
                CountryListContent(
                    countries = countries,
                    onCountryClick = onCountryClick
                )
            }
        }

        // Pull-to-refresh indicator
        PullRefreshIndicator(
            refreshing = isRefreshing,
            state = pullRefreshState,
            modifier = Modifier.align(Alignment.TopCenter)
        )
    }
}