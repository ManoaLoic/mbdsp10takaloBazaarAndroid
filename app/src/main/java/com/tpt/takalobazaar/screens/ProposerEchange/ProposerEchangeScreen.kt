package com.tpt.takalobazaar.screens.ProposerEchange

import android.widget.Toast
import androidx.compose.foundation.Image
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import coil.compose.rememberImagePainter
import com.tpt.takalobazaar.components.MyObjectsModal
import com.tpt.takalobazaar.components.ObjectSection
import com.tpt.takalobazaar.models.Object

@Composable
fun ProposerEchangeScreen(
    navController: NavHostController,
    userId: Int,
    objectId: Int? = null,
    viewModel: ProposerEchangeViewModel = hiltViewModel()
) {
    var isProposing by remember { mutableStateOf(false) }
    var showModal by remember { mutableStateOf(false) }
    var selectedUserId by remember { mutableStateOf(0) }

    val receiverObjects = remember { mutableStateListOf<Object>() }
    val proposerObjects = remember { mutableStateListOf<Object>() }

    val obj by viewModel.obj.collectAsState()
    val targetUser by viewModel.targetUser.collectAsState()
    val sessionUser by viewModel.sessionUser.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()

    val context = LocalContext.current

    LaunchedEffect(userId, objectId) {
        if (objectId == null || objectId == 0) {
            viewModel.initializeWithoutObject(userId)
        } else {
            viewModel.initializeWithObject(userId, objectId)
        }
    }

    LaunchedEffect(obj) {
        obj?.let { receiverObjects.add(it) }
    }

    fun handleProposeExchange() {
        if (receiverObjects.isEmpty() || proposerObjects.isEmpty()) {
            isProposing = false
            Toast.makeText(context, "Les deux listes doivent contenir au moins un objet.", Toast.LENGTH_LONG).show()
            return
        }

        viewModel.proposeExchange(
            proposerObjects = proposerObjects,
            receiverObjects = receiverObjects,
            onSuccess = { message, exchange ->
                isProposing = false
                Toast.makeText(context, message, Toast.LENGTH_LONG).show()
                navController.navigate("ficheExchange/${exchange?.id}")
            },
            onError = { message ->
                isProposing = false
                Toast.makeText(context, message, Toast.LENGTH_LONG).show()
            }
        )
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
                        handleProposeExchange()
                    },
                    colors = ButtonDefaults.buttonColors(backgroundColor = MaterialTheme.colors.primary),
                    modifier = Modifier.weight(1f),
                    enabled = !isProposing
                ) {
                    if (isProposing) {
                        CircularProgressIndicator(modifier = Modifier.size(24.dp))
                    } else {
                        Icon(imageVector = Icons.Default.Check, contentDescription = "Proposer")
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Proposer")
                    }
                }
                Spacer(modifier = Modifier.width(16.dp))
                OutlinedButton(
                    onClick = { navController.popBackStack() },
                    colors = ButtonDefaults.outlinedButtonColors(
                        backgroundColor = Color.Black,
                        contentColor = Color.White
                    ),
                    modifier = Modifier.weight(1f)
                ) {
                    Icon(imageVector = Icons.Default.Close, contentDescription = "Annuler")
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Annuler")
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
                                        painter = rememberImagePainter(sessionUser?.profilePicture),
                                        contentDescription = null,
                                        contentScale = ContentScale.Crop,
                                        modifier = Modifier
                                            .size(64.dp)
                                            .clip(CircleShape)
                                    )
                                    Text(
                                        text = sessionUser?.username ?: "",
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
                                        painter = rememberImagePainter(targetUser?.profilePicture),
                                        contentDescription = null,
                                        contentScale = ContentScale.Crop,
                                        modifier = Modifier
                                            .size(64.dp)
                                            .clip(CircleShape)
                                    )
                                    Text(
                                        text = targetUser?.username ?: "",
                                        modifier = Modifier.align(Alignment.CenterHorizontally)
                                    )
                                }
                            }
                        }
                        item {
                            ObjectSection(
                                title = "Objet(s) à échanger",
                                objects = receiverObjects,
                                navController = navController,
                                onAddObjectClick = {
                                    selectedUserId = targetUser?.id ?: 0
                                    showModal = true
                                },
                                onRemoveObjectClick = { selectedObject ->
                                    receiverObjects.remove(selectedObject)
                                }
                            )
                        }
                        item {
                            ObjectSection(
                                title = "Objet(s) en échange",
                                objects = proposerObjects,
                                navController = navController,
                                onAddObjectClick = {
                                    selectedUserId = sessionUser?.id ?: 0
                                    showModal = true
                                },
                                onRemoveObjectClick = { selectedObject ->
                                    proposerObjects.remove(selectedObject)
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
            existingObjects = if (selectedUserId == targetUser?.id) receiverObjects else proposerObjects,
            onObjectSelected = { selectedObject ->
                if (selectedUserId == targetUser?.id) {
                    receiverObjects.add(selectedObject)
                } else {
                    proposerObjects.add(selectedObject)
                }
            }
        )
    }
}