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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import coil.compose.rememberImagePainter
import com.mustfaibra.roffu.components.MyObjectsModal
import com.mustfaibra.roffu.components.ObjectCard
import com.mustfaibra.roffu.components.ObjectSection
import com.mustfaibra.roffu.models.Object

@Composable
fun ProposerEchangeScreen(
    navController: NavHostController,
    objectId: Int,
    viewModel: ProposerEchangeViewModel = hiltViewModel()
) {
    var isProposing by remember { mutableStateOf(false) }
    var showModal by remember { mutableStateOf(false) }
    var selectedUserId by remember { mutableStateOf(0) } // This will hold the userId for the modal

    val proposerObjects = remember { mutableStateListOf<Object>() }
    val receiverObjects = remember { mutableStateListOf<Object>() }

    // Fetch the object details
    LaunchedEffect(objectId) {
        viewModel.fetchObjectById(objectId)
    }

    val obj by viewModel.obj.collectAsState()
    val user by viewModel.user.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()

    // Add the passed object to the proposerObjects list initially
    LaunchedEffect(obj) {
        obj?.let { proposerObjects.add(it) }
    }

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
            Box(
                modifier = Modifier
                    .padding(innerPadding)
                    .fillMaxSize()
                    .padding(16.dp)
            ) {
                if (isLoading) {
                    CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                } else {
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
                                        painter = rememberImagePainter(obj?.user?.profilePicture),
                                        contentDescription = null,
                                        contentScale = ContentScale.Crop,
                                        modifier = Modifier
                                            .size(64.dp)
                                            .clip(CircleShape)
                                    )
                                    Text(
                                        text = obj?.user?.username ?: "",
                                        modifier = Modifier.align(Alignment.CenterHorizontally)
                                    )
                                }
                            }
                        }
                        item {
                            ObjectSection(
                                title = "Objet(s) à échanger",
                                objects = proposerObjects,
                                navController = navController,
                                onAddObjectClick = {
                                    selectedUserId = obj?.user?.id ?: 0
                                    showModal = true
                                },
                                onRemoveObjectClick = { selectedObject ->
                                    proposerObjects.remove(selectedObject)
                                }
                            )
                        }
                        item {
                            ObjectSection(
                                title = "Objet(s) en échange",
                                objects = receiverObjects,
                                navController = navController,
                                onAddObjectClick = {
                                    selectedUserId = viewModel.user.value?.id ?: 0
                                    showModal = true
                                },
                                onRemoveObjectClick = { selectedObject ->
                                    receiverObjects.remove(selectedObject)
                                }
                            )
                        }
                    }
                }
            }
        }
    )

    if (showModal && selectedUserId != 0) {
        MyObjectsModal(
            userId = selectedUserId,
            onDismiss = { showModal = false },
            navController = navController,
            objectService = viewModel.objectService,
            onObjectSelected = { selectedObject ->
                if (selectedUserId == obj?.user?.id) {
                    proposerObjects.add(selectedObject)
                } else {
                    receiverObjects.add(selectedObject)
                }
            }
        )
    }
}