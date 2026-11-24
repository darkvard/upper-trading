package com.upper.trading.core.network.util

import com.upper.trading.core.network.model.ApiException
import com.upper.trading.core.network.model.ErrorResponseDto
import kotlinx.serialization.json.Json
import retrofit2.HttpException
import java.io.IOException

/**
 * Executes a network call safely by parsing OpenAPI 3.0 error response schemas
 * and network-level exceptions into domain-friendly [ApiException].
 *
 * @param json Kotlinx Serialization [Json] instance for decoding error payloads.
 * @param block The suspend network call to execute.
 */
suspend inline fun <T> safeApiCall(
    json: Json,
    crossinline block: suspend () -> T
): T {
    return try {
        block()
    } catch (e: HttpException) {
        val errorBody = e.response()?.errorBody()?.string()
        val parsedError = if (!errorBody.isNullOrEmpty()) {
            runCatching { json.decodeFromString<ErrorResponseDto>(errorBody) }.getOrNull()
        } else null

        if (parsedError != null) {
            throw ApiException(
                code = parsedError.code,
                message = parsedError.message,
                traceId = parsedError.traceId
            )
        } else {
            throw ApiException(
                code = "HTTP_${e.code()}",
                message = e.message() ?: "HTTP error ${e.code()}"
            )
        }
    } catch (e: IOException) {
        throw ApiException(
            code = "NETWORK_ERROR",
            message = "No internet connection. Please check your network."
        )
    } catch (e: Exception) {
        if (e is ApiException) throw e
        throw ApiException(
            code = "UNKNOWN_ERROR",
            message = e.message ?: "An unexpected error occurred"
        )
    }
}
