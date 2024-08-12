package com.mustfaibra.roffu.screens.ExchangeHistory

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.mustfaibra.roffu.models.Exchange
import com.mustfaibra.roffu.sealed.Screen
import com.mustfaibra.roffu.services.SessionService
import com.mustfaibra.roffu.ui.theme.SecondColor
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Locale

@Composable
fun ExchangeHistoryScreen(
    navController: NavHostController,
    viewModel: ExchangeHistoryViewModel = hiltViewModel()
) {
    val exchanges by viewModel.exchanges.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val status by viewModel.status.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.loadExchangeHistory()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Historique des échanges") },
                backgroundColor = MaterialTheme.colors.secondary,
                contentColor = MaterialTheme.colors.onPrimary,
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Retour")
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            // Status Picklist
            StatusPicker(
                selectedStatus = status,
                onStatusSelected = { selectedStatus ->
                    viewModel.updateStatus(selectedStatus)
                }
            )

            // Exchange List or Loading Spinner or Empty Message
            val listState = rememberLazyListState()

            Box(modifier = Modifier.fillMaxSize()) {
                if (isLoading && exchanges.isEmpty()) {
                    // Centered loading indicator
                    Box(
                        modifier = Modifier
                            .fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator()
                    }
                } else if (exchanges.isEmpty()) {
                    // Empty state message
                    Text(
                        text = "Aucun échange trouvé",
                        modifier = Modifier.align(Alignment.Center),
                        textAlign = TextAlign.Center,
                        style = MaterialTheme.typography.h6
                    )
                } else {
                    LazyColumn(
                        state = listState,
                        modifier = Modifier.fillMaxSize()
                    ) {
                        items(exchanges.size) { index ->
                            ExchangeItem(
                                exchange = exchanges[index],
                                onClick = {
                                    navController.navigate("ficheExchange/${exchanges[index].id}")
                                }
                            )
                        }

                        // Show a loading indicator at the bottom when fetching more data
                        if (isLoading) {
                            item {
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(16.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    CircularProgressIndicator()
                                }
                            }
                        }
                    }
                }

                // Infinite Scroll
                LaunchedEffect(listState) {
                    snapshotFlow { listState.layoutInfo.visibleItemsInfo.lastOrNull()?.index }
                        .collect { lastVisibleItemIndex ->
                            if (lastVisibleItemIndex == exchanges.size - 1 && !isLoading) {
                                viewModel.loadMoreExchanges()
                            }
                        }
                }
            }
        }
    }
}

@Composable
fun StatusPicker(
    selectedStatus: String,
    onStatusSelected: (String) -> Unit
) {
    val statusOptions = listOf("Tous", "Accepté", "Refusé", "Annulé")
    var expanded by remember { mutableStateOf(false) }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        OutlinedButton(
            onClick = { expanded = !expanded },
            modifier = Modifier
                .fillMaxWidth()
                .shadow(4.dp, RoundedCornerShape(8.dp))
                .background(Color.White)
                .border(1.dp, Color.Gray, RoundedCornerShape(8.dp))
        ) {
            Text(text = selectedStatus, style = MaterialTheme.typography.subtitle1)
        }

        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            statusOptions.forEach { status ->
                DropdownMenuItem(onClick = {
                    expanded = false
                    onStatusSelected(status)
                }) {
                    Text(text = status)
                }
            }
        }
    }
}

@Composable
fun ExchangeItem(
    exchange: Exchange,
    onClick: () -> Unit
) {
    val statusColor = when (exchange.status) {
        "Refusé" -> Color.Red
        "Accepté" -> SecondColor
        "Annulé" -> Color(0xFFFFA500)
        else -> MaterialTheme.colors.onSurface
    }

    val dateFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault())
    val outputDateFormat = SimpleDateFormat("dd MMM yyyy HH:mm", Locale.getDefault())

    val formattedCreatedDate = try {
        val createdDate = dateFormat.parse(exchange.createdAt)
        outputDateFormat.format(createdDate)
    } catch (e: Exception) {
        "Unknown"
    }

    val formattedClosedDate = try {
        val closedDate = dateFormat.parse(exchange.date)
        outputDateFormat.format(closedDate)
    } catch (e: Exception) {
        ""
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
            .clickable { onClick() },
        elevation = 4.dp
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(text = "Proposeur: ${exchange.proposerUserName}", style = MaterialTheme.typography.body2)
            Text(text = "Récepteur: ${exchange.receiverUserName}", style = MaterialTheme.typography.body2)
            Row {
                Text(text = "Statut: ", style = MaterialTheme.typography.body2)
                Text(text = exchange.status, style = MaterialTheme.typography.body2.copy(color = statusColor))
            }
            Text(text = "Note: ${exchange.note ?: "N/A"}", style = MaterialTheme.typography.body2)
            Text(text = "Créé le: $formattedCreatedDate", style = MaterialTheme.typography.body2)
            Text(text = "Clôturé le: $formattedClosedDate", style = MaterialTheme.typography.body2)
        }
    }
}