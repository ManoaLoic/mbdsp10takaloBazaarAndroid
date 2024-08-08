package com.mustfaibra.roffu.screens.splash

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.hilt.navigation.compose.hiltViewModel
import com.mustfaibra.roffu.R
import com.mustfaibra.roffu.sealed.Screen
import kotlinx.coroutines.delay

@Composable
fun SplashScreen(
    splashViewModel: SplashViewModel = hiltViewModel(),
    onSplashFinished: (nextDestination: Screen) -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colors.background),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {

        val isAppLaunchedBefore by splashViewModel.isAppLaunchedBefore
            .collectAsState(initial = false)

        LaunchedEffect(key1 = Unit) {
            delay(3000)
            splashViewModel.checkLoggedUser { loggedUser ->
                if (loggedUser != null) {
                    // User is logged in, navigate to Profile
                    onSplashFinished(Screen.Home)
                } else {
                    // User is not logged in, navigate to Login
                    onSplashFinished(Screen.Home)
                }
            }
        }

        // Afficher l'image de logo
        Image(
            painter = painterResource(id = R.drawable.logo), // Utilisez le nom de votre nouvelle image ici
            contentDescription = "Logo",
            modifier = Modifier.fillMaxSize() // Ajustez la taille et le placement si nécessaire
        )
    }
}