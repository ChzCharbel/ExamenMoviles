package com.app.examenmoviles

import android.app.Application
import dagger.hilt.android.HiltAndroidApp

/**
 * Application class for the COVID-19 app
 * Annotated with @HiltAndroidApp to enable Hilt dependency injection
 */
@HiltAndroidApp
class CovidApplication : Application()
