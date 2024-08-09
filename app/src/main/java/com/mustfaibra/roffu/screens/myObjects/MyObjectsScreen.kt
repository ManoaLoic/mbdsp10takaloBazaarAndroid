package com.mustfaibra.roffu.screens.myobjects

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.mustfaibra.roffu.models.Object
import com.mustfaibra.roffu.components.ObjectCard

@Composable
fun MyObjectsScreen(navController: NavHostController, viewModel: MyObjectsViewModel = hiltViewModel()) {
    val userObjects by viewModel.userObjects.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.getMyObjects()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Mes Objets") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp)
        ) {
            if (userObjects.isEmpty()) {
                Text("Aucun objet trouvé.")
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize()
                ) {
                    items(userObjects) { obj ->
                        ObjectCard(obj = obj, isRecent = false, navController = navController)
                    }
                }
            }
        }
    }
}
