package com.upper.trading.core.network.engine

import com.upper.trading.core.network.config.NetworkConfig
import okhttp3.Interceptor
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.Protocol
import okhttp3.Response
import okhttp3.ResponseBody.Companion.toResponseBody
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Custom OkHttp middleware that intercepts requests and provides simulated responses
 * when [NetworkConfig.isMockEnabled] is true.
 *
 * When [NetworkConfig.isMockEnabled] is false, requests bypass this middleware
 * and proceed directly to the real backend server.
 */
@Singleton
class NetworkSimulationInterceptor @Inject constructor(
    private val authSimulationDispatcher: AuthSimulationDispatcher
) : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()

        // 1. If mock mode is turned off, proceed directly to real backend
        if (!NetworkConfig.isMockEnabled) {
            return chain.proceed(request)
        }

        // 2. Simulate network delay (configurable, 0ms for unit tests)
        val delayMs = NetworkConfig.simulationDelayMs
        if (delayMs > 0) {
            try {
                Thread.sleep(delayMs)
            } catch (_: InterruptedException) {}
        }

        // 3. Dispatch to simulated domain dispatcher
        val simulationResult = authSimulationDispatcher.dispatch(request)
            ?: return chain.proceed(request)

        // 4. Build OkHttp Response matching OpenAPI 3.0 schema
        return Response.Builder()
            .request(request)
            .protocol(Protocol.HTTP_1_1)
            .code(simulationResult.statusCode)
            .message(if (simulationResult.statusCode in 200..299) "OK" else "Client/Server Error")
            .body(simulationResult.jsonBody.toResponseBody("application/json; charset=utf-8".toMediaType()))
            .addHeader("Content-Type", "application/json")
            .build()
    }
}
