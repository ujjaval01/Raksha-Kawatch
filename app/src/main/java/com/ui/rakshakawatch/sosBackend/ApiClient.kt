package com.ui.rakshakawatch.sosBackend

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object ApiClient {
    private const val BASE_URL = "https://sos-backend-production.up.railway.app/" // Replace with your Railway URL

    val instance: SosApiService by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(SosApiService::class.java)
    }
}
