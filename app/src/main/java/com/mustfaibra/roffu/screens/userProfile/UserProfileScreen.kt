package com.mustfaibra.roffu.screens.userprofile

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import coil.compose.rememberImagePainter
import com.mustfaibra.roffu.components.ObjectCard

@Composable
fun UserProfileScreen(navController: NavHostController, userId: Int) {
    val viewModel: UserProfileViewModel = hiltViewModel()
    val userState by viewModel.userState.collectAsState()

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
            if (userState is UserProfileViewModel.UserState.Loading) {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
            }
            else if (userState is UserProfileViewModel.UserState.Success) {
                val user = (userState as UserProfileViewModel.UserState.Success).user
                val objects = (userState as UserProfileViewModel.UserState.Success).objects

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    // Affichage de la photo de profil
                    Image(
                        painter = rememberImagePainter(user.profilePicture),
                        contentDescription = "Photo de profil",
                        modifier = Modifier
                            .size(100.dp)
                            .clip(CircleShape)
                            .align(Alignment.CenterHorizontally),
                        contentScale = ContentScale.Crop
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // Nom d'utilisateur
                    Text(
                        text = "${user.firstName} ${user.lastName}",
                        style = MaterialTheme.typography.h5,
                        modifier = Modifier.align(Alignment.CenterHorizontally)
                    )

                    // Email
                    Text(
                        text = user.email ?: "",
                        style = MaterialTheme.typography.body1,
                        modifier = Modifier.align(Alignment.CenterHorizontally)
                    )

                    // Sexe
                    Text(
                        text = "Sexe: ${if (user.gender.equals("male", ignoreCase = true)) "Homme" else "Femme"}",
                        style = MaterialTheme.typography.body1,
                        modifier = Modifier.align(Alignment.CenterHorizontally)
                    )

                    // Liste des objets de l'utilisateur
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = "Ses Objets:",
                        style = MaterialTheme.typography.h6,
                        color = Color(0xFF8A8F6A),
                        modifier = Modifier.padding(bottom = 8.dp)
                    )

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
                    }
                }
            }
            else if (userState is UserProfileViewModel.UserState.Error) {
                Text(
                    text = (userState as UserProfileViewModel.UserState.Error).message,
                    color = Color.Red,
                    modifier = Modifier.align(Alignment.Center)
                )
            }
        }
    }
}
