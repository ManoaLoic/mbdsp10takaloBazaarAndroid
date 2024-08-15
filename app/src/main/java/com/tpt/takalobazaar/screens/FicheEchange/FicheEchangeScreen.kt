package com.tpt.takalobazaar.screens.FicheEchangeScreen

import android.app.DatePickerDialog
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
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import coil.compose.rememberImagePainter
import com.tpt.takalobazaar.components.ObjectCard
import com.tpt.takalobazaar.models.Exchange
import com.tpt.takalobazaar.models.LoginUser
import com.tpt.takalobazaar.screens.FicheEchange.FicheExchangeViewModel
import kotlinx.coroutines.launch
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
    var globalError by remember { mutableStateOf<String?>(null) }

    var currentUser by remember { mutableStateOf<LoginUser?>(null) }
    var showAcceptDialog by remember { mutableStateOf(false) }
    var showRejectDialog by remember { mutableStateOf(false) }

    val scaffoldState = rememberScaffoldState()
    val coroutineScope = rememberCoroutineScope()

    LaunchedEffect(exchangeId) {
        exchangeViewModel.fetchExchangeById(exchangeId)
        currentUser = exchangeViewModel.sessionService.getUser()
    }

    Scaffold(
        scaffoldState = scaffoldState,
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
            if (currentUser?.id == exchange?.receiverUserId) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Button(
                        onClick = { showAcceptDialog = true },
                        colors = ButtonDefaults.buttonColors(backgroundColor = MaterialTheme.colors.primary),
                        modifier = Modifier.weight(1f)
                    ) {
                        Icon(imageVector = Icons.Default.Check, contentDescription = "Accepter")
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Accepter")
                    }
                    Spacer(modifier = Modifier.width(16.dp))
                    OutlinedButton(
                        onClick = { showRejectDialog = true },
                        colors = ButtonDefaults.outlinedButtonColors(
                            backgroundColor = MaterialTheme.colors.onSurface,
                            contentColor = Color.White
                        ),
                        modifier = Modifier.weight(1f)
                    ) {
                        Icon(imageVector = Icons.Default.Close, contentDescription = "Refuser")
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Refuser")
                    }
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
                    val outputDateFormat = SimpleDateFormat("dd MMM yyyy hh:mm", Locale.getDefault())
                    val formattedDate = try {
                        val date = dateFormat.parse(ex.createdAt)
                        outputDateFormat.format(date)
                    } catch (e: Exception) {
                        "Unknown"
                    }

                    val formattedAppointmentDate = try {
                        val appointmentDate = ex.appointmentDate?.let { dateFormat.parse(it) }
                        appointmentDate?.let { outputDateFormat.format(it) } ?: "N/A"
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
                                style = MaterialTheme.typography.h6.copy(fontWeight = FontWeight.Bold)
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
                                    style = MaterialTheme.typography.h6.copy(fontWeight = FontWeight.Bold),
                                    color = MaterialTheme.colors.onSecondary
                                )
                            }
                        }
                        items(propositionObjects.chunked(2)) { rowItems ->
                            Row(modifier = Modifier.fillMaxWidth()) {
                                rowItems.forEach { exchangeObject ->
                                    Box(
                                        modifier = Modifier
                                            .padding(8.dp)
                                            .width(LocalConfiguration.current.screenWidthDp.dp / 2 - 24.dp)
                                    ) {
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
                                    style = MaterialTheme.typography.h6.copy(fontWeight = FontWeight.Bold),
                                    color = MaterialTheme.colors.onSecondary
                                )
                            }
                        }
                        items(receiverObjects.chunked(2)) { rowItems ->
                            Row(modifier = Modifier.fillMaxWidth()) {
                                rowItems.forEach { exchangeObject ->
                                    Box(
                                        modifier = Modifier
                                            .width(LocalConfiguration.current.screenWidthDp.dp / 2 - 24.dp)
                                            .padding(8.dp)
                                    ) {
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
                                        style = MaterialTheme.typography.h6.copy(fontWeight = FontWeight.Bold),
                                        color = MaterialTheme.colors.onSecondary
                                    )
                                }
                            }
                        }
                        item {
                            Column(modifier = Modifier.padding(8.dp)) {
                                Text(
                                    text = "Depuis : $formattedDate",
                                    style = MaterialTheme.typography.body1.copy(fontWeight = FontWeight.Bold)
                                )
                                Text(
                                    text = "Lieu du rendez-vous : ${ex.meetingPlace ?: "N/A"}",
                                    style = MaterialTheme.typography.body1.copy(fontWeight = FontWeight.Bold)
                                )
                                Text(
                                    text = "Note : ${ex.note ?: "N/A"}",
                                    style = MaterialTheme.typography.body1.copy(fontWeight = FontWeight.Bold)
                                )
                                Text(
                                    text = "Date du rendez-vous : ${formattedAppointmentDate ?: "N/A"}",
                                    style = MaterialTheme.typography.body1.copy(fontWeight = FontWeight.Bold)
                                )
                            }
                        }
                    }
                } ?: Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text(text = "Détails de l'échange non disponibles.")
                }
            }
        }

        if (showAcceptDialog) {
            AcceptExchangeDialog(
                onDismiss = { showAcceptDialog = false },
                onConfirm = { meetingPlace, appointmentDate ->
                    exchangeViewModel.acceptExchange(
                        exchangeId = exchangeId,
                        meetingPlace = meetingPlace,
                        appointmentDate = appointmentDate,
                        onSuccess = {
                            showAcceptDialog = false
                            coroutineScope.launch {
                                scaffoldState.snackbarHostState.showSnackbar("Échange accepté avec succès!")
                            }
                            navController.popBackStack()
                        },
                        onError = { errorMessage ->
                            showAcceptDialog = false
                            coroutineScope.launch {
                                scaffoldState.snackbarHostState.showSnackbar(errorMessage)
                            }
                        }
                    )
                },
                isLoading = isLoading,
                globalError = globalError
            )
        }
    }
}


@Composable
fun AcceptExchangeDialog(
    onDismiss: () -> Unit,
    onConfirm: (meetingPlace: String, appointmentDate: String) -> Unit,
    isLoading: Boolean,
    globalError: String? = null
) {
    var meetingPlace by remember { mutableStateOf("") }
    var appointmentDate by remember { mutableStateOf<Date?>(null) }
    var showError by remember { mutableStateOf(false) }
    val context = LocalContext.current
    val calendar = Calendar.getInstance()

    val datePickerDialog = DatePickerDialog(
        context,
        { _, year, month, dayOfMonth ->
            calendar.set(year, month, dayOfMonth)
            appointmentDate = calendar.time
        },
        calendar.get(Calendar.YEAR),
        calendar.get(Calendar.MONTH),
        calendar.get(Calendar.DAY_OF_MONTH)
    )

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(text = "Accepter l'échange") },
        text = {
            Column {
                OutlinedTextField(
                    value = meetingPlace,
                    onValueChange = { meetingPlace = it },
                    label = { Text("Lieu de rendez-vous") },
                    isError = showError && meetingPlace.isBlank(),
                    modifier = Modifier.fillMaxWidth()
                )
                if (showError && meetingPlace.isBlank()) {
                    Text(
                        text = "Le lieu de rendez-vous est requis",
                        color = MaterialTheme.colors.error,
                        style = MaterialTheme.typography.caption
                    )
                }
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = appointmentDate?.let { SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(it) } ?: "",
                    onValueChange = {},
                    label = { Text("Date de rendez-vous") },
                    readOnly = true,
                    trailingIcon = {
                        IconButton(onClick = { datePickerDialog.show() }) {
                            Icon(imageVector = Icons.Default.DateRange, contentDescription = "Sélectionner une date")
                        }
                    },
                    isError = showError && appointmentDate == null,
                    modifier = Modifier.fillMaxWidth()
                )
                if (showError && appointmentDate == null) {
                    Text(
                        text = "La date de rendez-vous est requise",
                        color = MaterialTheme.colors.error,
                        style = MaterialTheme.typography.caption
                    )
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (meetingPlace.isNotBlank() && appointmentDate != null) {
                        val formattedDate = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.getDefault()).format(appointmentDate!!)
                        onConfirm(meetingPlace, formattedDate)
                    } else {
                        showError = true
                    }
                },
                enabled = !isLoading
            ) {
                Text("Accepter")
            }
        },
        dismissButton = {
            Button(onClick = onDismiss,colors = ButtonDefaults.buttonColors(
                backgroundColor = Color.Black,
                contentColor = Color.White
            )) {
                Text("Annuler")
            }
        }
    )
}
