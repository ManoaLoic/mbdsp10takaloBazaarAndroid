package com.tpt.takalobazaar.screens.myobjects

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
import com.tpt.takalobazaar.models.Object
import com.tpt.takalobazaar.components.ObjectCard
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.ui.Alignment
import com.tpt.takalobazaar.screens.home.SearchField
import kotlinx.coroutines.flow.collectLatest

@Composable
fun MyObjectsScreen(navController: NavHostController, viewModel: MyObjectsViewModel = hiltViewModel()) {
    val userObjects by viewModel.userObjects.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val listState = rememberLazyGridState()

    var searchQuery by remember { mutableStateOf("") }

    LaunchedEffect(Unit) {
        viewModel.loadUserObjects(resetPage = true)
    }

    LaunchedEffect(listState) {
        snapshotFlow { listState.layoutInfo.visibleItemsInfo.lastOrNull() }
            .collectLatest { lastVisibleItem ->
                if (lastVisibleItem != null && lastVisibleItem.index == userObjects.size - 1 && !isLoading) {
                    viewModel.loadNextPage()
                }
            }
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
            // Search bar
            SearchField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                onFocusChange = { /* Handle focus change if needed */ },
                onImeActionClicked = {
                    viewModel.loadUserObjects(resetPage = true, searchQuery = searchQuery)
                },
                placeholder = "Recherche"
            )

            Spacer(modifier = Modifier.height(16.dp))

            when {
                isLoading && userObjects.isEmpty() -> {
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
                        state = listState,
                        columns = GridCells.Fixed(2),
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(4.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(userObjects) { obj ->
                            ObjectCard(obj = obj, isRecent = false, navController = navController, isMyObject = true)
                        }

                        if (isLoading && viewModel.hasMorePages.value) {
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
                }
            }
        }
    }
}