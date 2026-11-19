package com.upper.trading.presentation.features.auth.signin

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel


data class AuthSignInStyle(
    val bodyPadding: Dp = 16.dp,
    val itemPadding: Dp = 24.dp
)

object AuthSignInDefault {
    val style: AuthSignInStyle = AuthSignInStyle()
}

@Composable
fun AuthSignInRoute(
    
    viewmodel: AuthSignInViewModel = hiltViewModel()
) {

}

