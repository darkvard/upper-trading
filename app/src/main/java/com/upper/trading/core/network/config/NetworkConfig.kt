package com.upper.trading.core.network.config

/**
 * Global Network Configuration.
 *
 * Controls whether the network engine operates in Simulation (Mock) mode or Real Remote mode.
 * Default is [isMockEnabled] = true so anyone cloning the repository can immediately run and test
 * the app without needing a local backend or remote API gateway setup.
 */
object NetworkConfig {

    /**
     * Set to true to route API calls through [NetworkSimulationInterceptor].
     * Set too false to send real HTTP requests to [REAL_BASE_URL].
     */
    var isMockEnabled: Boolean = true

    /**
     * Network simulation delay in milliseconds.
     * Useful to test loading indicators, shimmer, and animations.
     * Can be set to 0 in unit tests.
     */
    var simulationDelayMs: Long = 800L

    /**
     * Real production or development Golang API Gateway base URL.
     */
    const val REAL_BASE_URL: String = "http://localhost:8080/api/v1/"

    /**
     * Mock placeholder URL (intercepted before network socket creation).
     */
    const val MOCK_BASE_URL: String = "https://mock.uppertrading.dev/api/v1/"

    fun getBaseUrl(): String = if (isMockEnabled) MOCK_BASE_URL else REAL_BASE_URL
}
