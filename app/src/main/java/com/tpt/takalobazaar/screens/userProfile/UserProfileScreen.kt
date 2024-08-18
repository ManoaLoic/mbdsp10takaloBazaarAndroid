package com.tpt.takalobazaar.screens.userprofile

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import coil.compose.rememberImagePainter
import com.tpt.takalobazaar.components.ObjectCard
import com.tpt.takalobazaar.screens.home.SearchField
import com.tpt.takalobazaar.screens.profile.ProfileHeaderSection
import kotlinx.coroutines.flow.collectLatest

@Composable
fun UserProfileScreen(navController: NavHostController, userId: Int) {
    val viewModel: UserProfileViewModel = hiltViewModel()
    val userState by viewModel.userState.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val hasMorePages by viewModel.hasMorePages.collectAsState()
    var searchQuery by remember { mutableStateOf("") }
    var isSearching by remember { mutableStateOf(false) }

    LaunchedEffect(userId) {
        viewModel.fetchUserById(userId)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Profil Utilisateur") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Retour")
                    }
                }
            )
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            if (userState is UserProfileViewModel.UserState.Loading || isLoading) {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
            } else if (userState is UserProfileViewModel.UserState.Success) {
                val user = (userState as UserProfileViewModel.UserState.Success).user
                val objects = (userState as UserProfileViewModel.UserState.Success).objects

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    ProfileHeaderSection(
                        image = user.profilePicture,
                        name = "${user.firstName} ${user.lastName}",
                        email = user.email,
                        username = user.username,
                        onImageClicked = { }
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    SearchField(
                        value = searchQuery,
                        onValueChange = { newValue -> searchQuery = newValue },
                        onFocusChange = { /* Handle focus change if needed */ },
                        onImeActionClicked = {
                            isSearching = true
                            viewModel.searchObjects(userId, searchQuery)
                            isSearching = false
                        },
                        placeholder = "Recherche"
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    LazyVerticalGrid(
                        columns = GridCells.Fixed(2),
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(4.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(objects) { obj ->
                            ObjectCard(
                                obj = obj,
                                isRecent = false,
                                navController = navController,
                                disableNavigation = false
                            )
                        }

                        if (isLoading && hasMorePages) {
                            item {
                                CircularProgressIndicator(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(50.dp)
                                        .padding(16.dp),
                                    strokeWidth = 4.dp
                                )
                            }
                        }
                    }

                    val listState = rememberLazyGridState()

                    LaunchedEffect(listState) {
                        snapshotFlow { listState.layoutInfo.visibleItemsInfo.lastOrNull() }
                            .collectLatest { lastVisibleItem ->
                                if (lastVisibleItem != null && lastVisibleItem.index == objects.size - 1 && hasMorePages && !isLoading) {
                                    viewModel.loadNextPage(userId)
                                }
                            }
                    }
                }
            } else if (userState is UserProfileViewModel.UserState.Error) {
                Text(
                    text = (userState as UserProfileViewModel.UserState.Error).message,
                    color = Color.Red,
                    modifier = Modifier.align(Alignment.Center)
                )
            }
        }
    }
}