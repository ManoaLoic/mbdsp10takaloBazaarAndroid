package com.tpt.takalobazaar.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.navigation.NavHostController
import com.tpt.takalobazaar.api.ObjectService
import com.tpt.takalobazaar.models.Object
import com.tpt.takalobazaar.screens.home.SearchField
import kotlinx.coroutines.flow.collectLatest
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
    var searchQuery by remember { mutableStateOf("") }
    var page by remember { mutableStateOf(1) }
    var hasMorePages by remember { mutableStateOf(true) }
    val screenHeight = LocalConfiguration.current.screenHeightDp.dp
    val coroutineScope = rememberCoroutineScope()

    // Function to load objects from API
    fun loadObjects() {
        coroutineScope.launch {
            val params = mutableMapOf(
                "page" to page.toString(),
                "limit" to "20",
                "status" to "Available"
            )

            // Add search query to params if it's not empty
            if (searchQuery.isNotBlank()) {
                params["name"] = searchQuery
            }

            val response = objectService.getUserObjects(userId, params)
            if (response.isSuccessful) {
                val newObjects = response.body()?.data?.objects?.filter { obj ->
                    existingObjects.none { it.id == obj.id }
                } ?: emptyList()

                if (newObjects.isNotEmpty()) {
                    if (page == 1) {
                        userObjects = newObjects
                    } else {
                        userObjects = userObjects + newObjects
                    }
                }
                hasMorePages = response.body()?.data?.currentPage ?: 1 < response.body()?.data?.totalPages ?: 1
            }
            isLoading = false
        }
    }

    // Load initial objects
    LaunchedEffect(userId, page) {
        loadObjects()
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

                // Search bar
                SearchField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    onFocusChange = { /* Handle focus change if needed */ },
                    onImeActionClicked = {
                        // Reset pagination and search
                        page = 1
                        hasMorePages = true
                        userObjects = emptyList()
                        isLoading = true
                        loadObjects()
                    },
                    placeholder = "Recherche"
                )

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
                        val listState = rememberLazyGridState()

                        LazyVerticalGrid(
                            state = listState,
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

                            if (isLoading && hasMorePages) {
                                item(span = { GridItemSpan(2) }) {
                                    Box(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .height(50.dp),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        CircularProgressIndicator()
                                    }
                                }
                            }
                        }

                        // Infinite scroll implementation
                        LaunchedEffect(listState) {
                            coroutineScope.launch {
                                snapshotFlow { listState.layoutInfo.visibleItemsInfo.lastOrNull() }
                                    .collectLatest { lastVisibleItem ->
                                        if (lastVisibleItem != null && lastVisibleItem.index == userObjects.size - 1 && hasMorePages && !isLoading) {
                                            page++
                                            isLoading = true
                                            loadObjects()
                                        }
                                    }
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