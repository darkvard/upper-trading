package com.upper.trading.core.network.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ErrorResponseDto(
    @SerialName("code") val code: String,
    @SerialName("message") val message: String,
    @SerialName("trace_id") val traceId: String? = null
)

class ApiException(
    val code: String,
    override val message: String,
    val traceId: String? = null
) : Exception(message)
