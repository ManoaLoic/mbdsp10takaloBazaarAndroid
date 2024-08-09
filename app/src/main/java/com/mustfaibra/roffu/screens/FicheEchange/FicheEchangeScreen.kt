package com.mustfaibra.roffu.screens.FicheEchangeScreen

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
import com.mustfaibra.roffu.models.Exchange
import com.mustfaibra.roffu.screens.FicheEchange.FicheExchangeViewModel
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun FicheExchangeScreen(
    navController: NavHostController,
    exchangeId: Int,
    exchangeViewModel: FicheExchangeViewModel = hiltViewModel()
) {
    val exchange by exchangeViewModel.getExchangeById(exchangeId).collectAsState(initial = null)
    val isLoading by exchangeViewModel.isLoading.collectAsState()

    LaunchedEffect(exchangeId) {
        exchangeViewModel.fetchExchangeById(exchangeId)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(text = "Détails de l'échange") },
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
                    onClick = { /* Handle accept action */ },
                    colors = ButtonDefaults.buttonColors(backgroundColor = MaterialTheme.colors.primary),
                    modifier = Modifier.weight(1f)
                ) {
                    Icon(imageVector = Icons.Default.Check, contentDescription = "Accepter")
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Accepter")
                }
                Spacer(modifier = Modifier.width(16.dp))
                OutlinedButton(
                    onClick = { /* Handle reject action */ },
                    colors = ButtonDefaults.outlinedButtonColors(backgroundColor = Color.White),
                    modifier = Modifier.weight(1f)
                ) {
                    Icon(imageVector = Icons.Default.Close, contentDescription = "Refuser")
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Refuser")
                }
            }
        }
    ) { innerPadding ->
        Box(modifier = Modifier.padding(innerPadding)) {
            if (isLoading) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            } else {
                exchange?.let { ex ->
                    val dateFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault())
                    val outputDateFormat = SimpleDateFormat("dd MMM yyyy", Locale.getDefault())
                    val formattedDate = try {
                        val date = dateFormat.parse(ex.createdAt)
                        outputDateFormat.format(date)
                    } catch (e: Exception) {
                        "Unknown"
                    }

                    // Divide the exchange objects into two lists
                    val propositionObjects = ex.exchangeObjects?.filter { it.userId == ex.proposerUserId } ?: emptyList()
                    val receiverObjects = ex.exchangeObjects?.filter { it.userId == ex.receiverUserId } ?: emptyList()

                    LazyColumn(
                        contentPadding = PaddingValues(16.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        item {
                            Text(
                                text = "Status: ${ex.status} ($formattedDate)",
                                style = MaterialTheme.typography.h6
                            )
                        }
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
                                        painter = rememberImagePainter(ex.proposer?.profilePicture),
                                        contentDescription = null,
                                        contentScale = ContentScale.Crop,
                                        modifier = Modifier
                                            .size(64.dp)
                                            .clip(CircleShape)
                                    )
                                    Text(
                                        text = "${ex.proposer?.username}",
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
                                        painter = rememberImagePainter(ex.receiver?.profilePicture),
                                        contentDescription = null,
                                        contentScale = ContentScale.Crop,
                                        modifier = Modifier
                                            .size(64.dp)
                                            .clip(CircleShape)
                                    )
                                    Text(
                                        text = "${ex.receiver?.username}",
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
                        items(propositionObjects.chunked(2)) { rowItems ->
                            Row(modifier = Modifier.fillMaxWidth()) {
                                rowItems.forEach { exchangeObject ->
                                    Box(modifier = Modifier.weight(1f).padding(8.dp)) {
                                        ObjectCard(exchangeObject.obj, false, navController)
                                    }
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
                                    text = "Contre",
                                    style = MaterialTheme.typography.h6,
                                    color = MaterialTheme.colors.onSecondary
                                )
                            }
                        }
                        items(receiverObjects.chunked(2)) { rowItems ->
                            Row(modifier = Modifier.fillMaxWidth()) {
                                rowItems.forEach { exchangeObject ->
                                    Box(modifier = Modifier.weight(1f).padding(8.dp)) {
                                        ObjectCard(exchangeObject.obj, false, navController)
                                    }
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
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        imageVector = Icons.Default.Info,
                                        contentDescription = "Détails",
                                        modifier = Modifier.size(24.dp),
                                        tint = MaterialTheme.colors.onSecondary
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(
                                        text = "Détails",
                                        style = MaterialTheme.typography.h6,
                                        color = MaterialTheme.colors.onSecondary
                                    )
                                }
                            }
                        }
                        item {
                            Column(modifier = Modifier.padding(8.dp)) {
                                Text(text = "Depuis : $formattedDate", style = MaterialTheme.typography.body1)
                                Text(text = "Lieu du rendez-vous : ${ex.meetingPlace ?: "N/A"}", style = MaterialTheme.typography.body1)
                                Text(text = "Note : ${ex.note ?: "N/A"}", style = MaterialTheme.typography.body1)
                                Text(text = "Date du rendez-vous : ${ex.appointmentDate ?: "N/A"}", style = MaterialTheme.typography.body1)
                            }
                        }
                    }
                } ?: Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    if (exchange == null) {
                        Text(text = "Exchange details not available.")
                    }
                }
            }
        }
    }
}
