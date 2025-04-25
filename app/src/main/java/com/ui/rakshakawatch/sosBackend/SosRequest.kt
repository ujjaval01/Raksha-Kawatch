package com.ui.rakshakawatch.sosBackend

// models/SosRequest.kt
data class SosRequest(
    val message: String,
    val location: String,
    val to: List<String>
)

