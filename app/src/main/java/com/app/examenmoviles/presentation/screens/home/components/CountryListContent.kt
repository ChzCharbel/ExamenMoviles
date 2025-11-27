package com.app.examenmoviles.presentation.screens.home.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.app.examenmoviles.domain.model.CountryCovid

/**
 * Content for displaying the list of countries
 */
@Composable
fun CountryListContent(
    countries: List<CountryCovid>,
    onCountryClick: (CountryCovid) -> Unit,
    modifier: Modifier = Modifier
) {
    LazyColumn(
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        items(
            items = countries,
            key = { it.country + it.region }
        ) { country ->
            CountryCard(
                country = country,
                onClick = { onCountryClick(country) }
            )
        }
    }
}