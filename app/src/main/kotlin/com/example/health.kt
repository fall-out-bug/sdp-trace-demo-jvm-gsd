package com.example

import java.time.Instant

data class HealthResponse(
    val status: String,
    val timestamp: Long
) {
    companion object {
        fun ok() = HealthResponse(
            status = "ok",
            timestamp = Instant.now().toEpochMilli()
        )
    }
}