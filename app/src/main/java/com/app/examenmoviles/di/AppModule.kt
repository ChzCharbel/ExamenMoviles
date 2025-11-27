package com.app.examenmoviles.di

import com.app.examenmoviles.data.remote.api.CovidApi
import com.app.examenmoviles.data.repository.CovidRepositoryImpl
import com.app.examenmoviles.domain.repository.CovidRepository
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit
import javax.inject.Singleton

/**
 * Hilt module for providing application-level dependencies
 * Includes Retrofit, OkHttp, API, and Repository instances
 */
@Module
@InstallIn(SingletonComponent::class)
object AppModule {
    private const val BASE_URL = "https://api.api-ninjas.com/"
    private const val API_KEY = "5Vcr1hF1SH0wXVUy89GOkg==hpJzHXsJIYwctvkx"

    /**
     * Provides Gson instance for JSON serialization/deserialization
     */
    @Provides
    @Singleton
    fun provideGson(): Gson =
        GsonBuilder()
            .setLenient()
            .create()

    /**
     * Provides API Key Interceptor to add X-Api-Key header to all requests
     */
    @Provides
    @Singleton
    fun provideApiKeyInterceptor(): Interceptor =
        Interceptor { chain ->
            val originalRequest = chain.request()
            val requestWithApiKey =
                originalRequest
                    .newBuilder()
                    .addHeader("X-Api-Key", API_KEY)
                    .build()
            chain.proceed(requestWithApiKey)
        }

    /**
     * Provides HTTP Logging Interceptor for debugging network requests
     */
    @Provides
    @Singleton
    fun provideLoggingInterceptor(): HttpLoggingInterceptor =
        HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }

    /**
     * Provides OkHttpClient with interceptors and timeout configurations
     */
    @Provides
    @Singleton
    fun provideOkHttpClient(
        apiKeyInterceptor: Interceptor,
        loggingInterceptor: HttpLoggingInterceptor,
    ): OkHttpClient =
        OkHttpClient
            .Builder()
            .addInterceptor(apiKeyInterceptor)
            .addInterceptor(loggingInterceptor)
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .writeTimeout(30, TimeUnit.SECONDS)
            .build()

    /**
     * Provides Retrofit instance configured with base URL and converters
     */
    @Provides
    @Singleton
    fun provideRetrofit(
        okHttpClient: OkHttpClient,
        gson: Gson,
    ): Retrofit =
        Retrofit
            .Builder()
            .baseUrl(BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create(gson))
            .build()

    /**
     * Provides CovidApi instance from Retrofit
     */
    @Provides
    @Singleton
    fun provideCovidApi(retrofit: Retrofit): CovidApi = retrofit.create(CovidApi::class.java)

    /**
     * Provides CovidRepository implementation
     * Binds CovidRepositoryImpl to CovidRepository interface
     */
    @Provides
    @Singleton
    fun provideCovidRepository(repositoryImpl: CovidRepositoryImpl): CovidRepository = repositoryImpl
}
