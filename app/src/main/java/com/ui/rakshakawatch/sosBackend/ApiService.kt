package com.ui.rakshakawatch.sosBackend
// api/ApiService.kt
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.POST

interface ApiService {
    @POST("send-sos")
    fun sendSos(@Body sosRequest: SosRequest): Call<SosResponse>
}

