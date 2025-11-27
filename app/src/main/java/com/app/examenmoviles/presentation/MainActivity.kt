package com.app.examenmoviles.presentation

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.ui.Modifier
import com.app.examenmoviles.presentation.navigation.CovidNavGraph
import com.app.examenmoviles.presentation.theme.ExamenMovilesTheme
import dagger.hilt.android.AndroidEntryPoint

/**
 * Main activity for the COVID-19 Tracker app
 * Entry point for the application with Hilt dependency injection
 */
@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            ExamenMovilesTheme {
                CovidNavGraph(
                    modifier = Modifier.fillMaxSize()
                )
            }
        }
    }
}
