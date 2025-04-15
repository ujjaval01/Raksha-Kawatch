package com.ui.rakshakawatch.sosBackend

import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.POST

interface SosApiService {
    @POST("/send-sos")
    fun sendSos(@Body request: SosRequest): Call<SosResponse>
}
