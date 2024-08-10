package com.mustfaibra.roffu.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.navigation.NavHostController
import com.mustfaibra.roffu.api.ObjectService
import com.mustfaibra.roffu.models.Object
import kotlinx.coroutines.launch

@Composable
fun MyObjectsModal(
    userId: Int,
    existingObjects: List<Object>, // List of objects already in the selected list
    onDismiss: () -> Unit,
    navController: NavHostController,
    onObjectSelected: (Object) -> Unit,
    objectService: ObjectService,
) {
    var userObjects by remember { mutableStateOf<List<Object>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }
    val screenHeight = LocalConfiguration.current.screenHeightDp.dp

    val coroutineScope = rememberCoroutineScope()

    LaunchedEffect(userId) {
        coroutineScope.launch {
            val params = mapOf("page" to 1, "limit" to 20)
            val response = objectService.getUserObjects(userId, params)
            if (response.isSuccessful) {
                // Filter out objects that are already in the existingObjects list
                userObjects = response.body()?.data?.objects?.filter { obj ->
                    existingObjects.none { it.id == obj.id }
                } ?: emptyList()
            }
            isLoading = false
        }
    }

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = MaterialTheme.shapes.medium,
            color = MaterialTheme.colors.surface,
            modifier = Modifier
                .fillMaxWidth()
                .height(screenHeight * 0.95f) // Set the height to 95% of the screen height
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp)
            ) {
                Text(text = "Choisissez un objet", style = MaterialTheme.typography.h6)

                Spacer(modifier = Modifier.height(16.dp))

                when {
                    isLoading -> {
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            CircularProgressIndicator()
                        }
                    }
                    userObjects.isEmpty() -> {
                        Text("Aucun objet trouvé.")
                    }
                    else -> {
                        LazyVerticalGrid(
                            columns = GridCells.Fixed(2),
                            modifier = Modifier.fillMaxSize(),
                            contentPadding = PaddingValues(4.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            items(userObjects) { obj ->
                                ObjectCard(
                                    obj = obj,
                                    isRecent = false,
                                    navController = navController,
                                    disableNavigation = true, // Disable navigation inside the modal
                                    onClick = {
                                        onObjectSelected(obj)
                                        onDismiss()
                                    }
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                Row(
                    horizontalArrangement = Arrangement.End,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    TextButton(onClick = onDismiss) {
                        Text("Fermer")
                    }
                }
            }
        }
    }
}
