package com.upper.trading.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSerializable
import androidx.navigation3.runtime.NavBackStack
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.runtime.serialization.NavBackStackSerializer

interface Navigator<T : NavKey> {
    val backStack: NavBackStack<T>

    fun push(destination: T)

    fun replace(destination: T)

    fun replaceAll(destination: T)

    fun pop(): Boolean
}

class NavigatorImpl<T : NavKey>(override val backStack: NavBackStack<T>) : Navigator<T> {

    override fun push(destination: T) {
        backStack.add(destination)
    }

    override fun replace(destination: T) {
        if (backStack.isNotEmpty()) {
            backStack[backStack.lastIndex] = destination
        } else {
            backStack.add(destination)
        }
    }

    override fun replaceAll(destination: T) {
        backStack.clear()
        backStack.add(destination)
    }

    override fun pop(): Boolean {
        if (backStack.size > 1) {
            backStack.removeAt(backStack.lastIndex)
            return true
        }
        return false
    }
}

@Composable
fun rememberAppNavigator(
    startDestination: AppNavKey = SplashNavKey
): Navigator<AppNavKey> {
    val backStack: NavBackStack<AppNavKey> = rememberSerializable(
        serializer = NavBackStackSerializer<AppNavKey>()
    ) {
        NavBackStack(startDestination)
    }

    return remember(backStack) { NavigatorImpl(backStack) }
}