package com.mustfaibra.roffu.screens.ficheobjet

import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController

@Composable
fun FicheObjetScreen(navController: NavHostController, objectId: Int) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Fiche Objet") }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text("Object created successfully with ID: $objectId")
            Spacer(modifier = Modifier.height(16.dp))
            Button(onClick = {
                // Navigate back to home or any other screen
                navController.navigate("home") {
                    popUpTo("home") { inclusive = true }
                }
            }) {
                Text("Go to Home")
            }
        }
    }
}
