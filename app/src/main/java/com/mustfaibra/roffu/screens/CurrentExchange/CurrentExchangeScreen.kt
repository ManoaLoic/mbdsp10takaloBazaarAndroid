package com.mustfaibra.roffu.screens.CurrentExchange

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.mustfaibra.roffu.models.Exchange
import com.mustfaibra.roffu.sealed.Screen
import com.mustfaibra.roffu.ui.theme.Dimension
import com.mustfaibra.roffu.viewmodels.ExchangeViewModel

@Composable
fun CurrentExchangeScreen(
    navController: NavHostController,
    exchangeViewModel: ExchangeViewModel = hiltViewModel()
) {
    val exchanges by exchangeViewModel.currentExchanges.collectAsState()
    val isLoading by exchangeViewModel.isLoading.collectAsState()

    LaunchedEffect(Unit) {
        exchangeViewModel.fetchCurrentExchanges()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(text = "Mes échanges en cours") },
                backgroundColor = MaterialTheme.colors.secondary,
                contentColor = MaterialTheme.colors.onPrimary,
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { innerPadding ->
        Box(modifier = Modifier.padding(innerPadding)) {
            if (isLoading) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            } else {
                Column(modifier = Modifier.fillMaxSize()) {
                    if (exchanges.isNotEmpty()) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp)
                                .padding(top = 16.dp)
                                .background(MaterialTheme.colors.primary, RoundedCornerShape(8.dp))
                                .clip(RoundedCornerShape(8.dp))
                                .padding(8.dp)
                        ) {
                            Text(
                                text = "Nombre d'échanges: ${exchanges.size}",
                                color = Color.White,
                                style = MaterialTheme.typography.subtitle1,
                                modifier = Modifier.align(Alignment.Center)
                            )
                        }
                    }

                    if (exchanges.isEmpty()) {
                        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                            Text(text = "No current exchanges available.")
                        }
                    } else {
                        LazyColumn(
                            contentPadding = PaddingValues(Dimension.pagePadding),
                            verticalArrangement = Arrangement.spacedBy(Dimension.pagePadding),
                            modifier = Modifier.fillMaxSize()
                        ) {
                            items(exchanges) { exchange ->
                                ExchangeCard(exchange = exchange) {
                                    navController.navigate("${Screen.FicheEchange.route}/${exchange.id}")
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun ExchangeCard(exchange: Exchange, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
            .padding(vertical = 8.dp)
            .clickable { onClick() }
            .shadow(8.dp, RoundedCornerShape(8.dp)),
        shape = RoundedCornerShape(8.dp),
        elevation = 4.dp,
        backgroundColor = MaterialTheme.colors.surface
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = "Exchange ID: ${exchange.id}",
                style = MaterialTheme.typography.subtitle1,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "Proposer: ${exchange.proposer?.username}",
                style = MaterialTheme.typography.body2
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = "Receiver: ${exchange.receiver?.username}",
                style = MaterialTheme.typography.body2
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = "Status: ${exchange.status}",
                style = MaterialTheme.typography.body2
            )
        }
    }
}
