package com.mustfaibra.roffu.screens.ProposerEchange

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import coil.compose.rememberImagePainter
import com.mustfaibra.roffu.components.ObjectCard
import com.mustfaibra.roffu.models.Object

@Composable
fun ProposerEchangeScreen(
    navController: NavHostController,
    objectId: Int,
    viewModel: ProposerEchangeViewModel = hiltViewModel()
) {
    var isProposing by remember { mutableStateOf(false) }

    // Fetch the object details
    LaunchedEffect(objectId) {
        viewModel.fetchObjectById(objectId)
    }

    val obj by viewModel.obj.collectAsState()
    val user by viewModel.user.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(text = "Proposer un Échange") },
                backgroundColor = MaterialTheme.colors.primary,
                contentColor = MaterialTheme.colors.onPrimary,
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        },
        bottomBar = {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Button(
                    onClick = {
                        isProposing = true
                        viewModel.proposeExchange(objectId)
                    },
                    colors = ButtonDefaults.buttonColors(backgroundColor = MaterialTheme.colors.primary),
                    modifier = Modifier.weight(1f)
                ) {
                    Icon(imageVector = Icons.Default.Check, contentDescription = "Proposer")
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Proposer")
                }
                Spacer(modifier = Modifier.width(16.dp))
                OutlinedButton(
                    onClick = { navController.popBackStack() },
                    colors = ButtonDefaults.outlinedButtonColors(backgroundColor = Color.Black),
                    modifier = Modifier.weight(1f)
                ) {
                    Icon(imageVector = Icons.Default.Close, contentDescription = "Annuler")
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Annuler", color = Color.White)
                }
            }
        },
        content = { innerPadding ->
            Box(modifier = Modifier.padding(innerPadding).fillMaxSize().padding(16.dp)) {
                if (isLoading) {
                    CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                } else {
                    obj?.let { obj ->
                        LazyColumn(
                            contentPadding = PaddingValues(16.dp),
                            verticalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            item {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Column(
                                        horizontalAlignment = Alignment.CenterHorizontally,
                                        modifier = Modifier.weight(5f / 12f)
                                    ) {
                                        Image(
                                            painter = rememberImagePainter(user?.profilePicture),
                                            contentDescription = null,
                                            contentScale = ContentScale.Crop,
                                            modifier = Modifier
                                                .size(64.dp)
                                                .clip(CircleShape)
                                        )
                                        Text(
                                            text = user?.username ?: "",
                                            modifier = Modifier.align(Alignment.CenterHorizontally)
                                        )
                                    }
                                    Icon(
                                        imageVector = Icons.Default.ArrowForward,
                                        contentDescription = "Arrow",
                                        modifier = Modifier
                                            .size(32.dp)
                                            .weight(2f / 12f)
                                    )
                                    Column(
                                        horizontalAlignment = Alignment.CenterHorizontally,
                                        modifier = Modifier.weight(5f / 12f)
                                    ) {
                                        Image(
                                            painter = rememberImagePainter(obj.user?.profilePicture),
                                            contentDescription = null,
                                            contentScale = ContentScale.Crop,
                                            modifier = Modifier
                                                .size(64.dp)
                                                .clip(CircleShape)
                                        )
                                        Text(
                                            text = obj.user?.username ?: "",
                                            modifier = Modifier.align(Alignment.CenterHorizontally)
                                        )
                                    }
                                }
                            }
                            item {
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .shadow(4.dp, MaterialTheme.shapes.medium)
                                        .background(MaterialTheme.colors.secondary)
                                        .clip(MaterialTheme.shapes.medium)
                                        .padding(8.dp)
                                ) {
                                    Text(
                                        text = "Proposition",
                                        style = MaterialTheme.typography.h6,
                                        color = MaterialTheme.colors.onSecondary
                                    )
                                }
                            }
                            item {
                                ObjectCard(obj, false, navController)
                            }
                        }
                    } ?: Text(text = "Object details not available.", modifier = Modifier.align(Alignment.Center))
                }
            }
        }
    )
}
