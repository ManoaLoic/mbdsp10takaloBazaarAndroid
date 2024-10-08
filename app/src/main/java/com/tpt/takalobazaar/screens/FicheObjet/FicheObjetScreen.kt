package com.tpt.takalobazaar.screens.ficheobjet

import androidx.compose.foundation.*
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.runtime.*
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
import com.tpt.takalobazaar.components.ObjectQRModal
import com.tpt.takalobazaar.models.LoginUser
import com.tpt.takalobazaar.models.Object
import com.tpt.takalobazaar.sealed.Screen
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun FicheObjetScreen(navController: NavHostController, objectId: Int) {
    val viewModel: FicheObjetViewModel = hiltViewModel()
    val objectState by viewModel.objectState.collectAsState()
    val reportTypes by viewModel.reportTypes.collectAsState()
    val isReporting by viewModel.isReporting.collectAsState()

    var currentUser by remember { mutableStateOf<LoginUser?>(null) }
    val scaffoldState = rememberScaffoldState()
    val coroutineScope = rememberCoroutineScope()
    var isLoading by remember { mutableStateOf(false) }
    var showReportModal by remember { mutableStateOf(false) }

    LaunchedEffect(objectId) {
        currentUser = viewModel.sessionService.getUser()
        viewModel.fetchObjectById(objectId)
        viewModel.fetchReportTypes()
    }

    Scaffold(
        scaffoldState = scaffoldState,
        topBar = {
            TopAppBar(
                title = { Text("Fiche Objet") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        },
        bottomBar = {
            when (objectState) {
                is FicheObjetViewModel.ObjectState.Success -> {
                    var obj by remember { mutableStateOf((objectState as FicheObjetViewModel.ObjectState.Success).obj) }
                    obj?.let {
                        if (it.userId != currentUser?.id) {
                            Button(
                                onClick = {
                                    navController.navigate("${Screen.ProposerEchange}?userId=${it.userId}&objectId=${it.id}")
                                },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(16.dp),
                                colors = ButtonDefaults.buttonColors(backgroundColor = Color(0xFFBC8246))
                            ) {
                                Text("Proposer un échange", color = Color.White)
                            }
                        }
                    }
                }

                else -> {}
            }
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            when (objectState) {
                is FicheObjetViewModel.ObjectState.Loading -> {
                    CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                }
                is FicheObjetViewModel.ObjectState.Success -> {
                    var obj by remember { mutableStateOf((objectState as FicheObjetViewModel.ObjectState.Success).obj) }
                    val categoryColor = Color(0xFFBC8246)

                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .verticalScroll(rememberScrollState())
                            .padding(bottom = 60.dp)
                    ) {
                        ObjectDetail(
                            obj = obj,
                            navController = navController,
                            isLoading = isLoading,
                            onToggleObjectStatus = {
                                isLoading = true
                                if (obj.status == "Available") {
                                    viewModel.removeObject(
                                        objectId = obj.id,
                                        onSuccess = {
                                            obj = obj.copy(status = "Removed")
                                            isLoading = false
                                            coroutineScope.launch {
                                                scaffoldState.snackbarHostState.showSnackbar("Objet retiré avec succès")
                                            }
                                        },
                                        onError = { errorMessage ->
                                            isLoading = false
                                            coroutineScope.launch {
                                                scaffoldState.snackbarHostState.showSnackbar("Erreur : $errorMessage")
                                            }
                                        }
                                    )
                                } else {
                                    viewModel.repostObject(
                                        objectId = obj.id,
                                        onSuccess = {
                                            obj = obj.copy(status = "Available")
                                            isLoading = false
                                            coroutineScope.launch {
                                                scaffoldState.snackbarHostState.showSnackbar("Objet republié avec succès")
                                            }
                                        },
                                        onError = { errorMessage ->
                                            isLoading = false
                                            coroutineScope.launch {
                                                scaffoldState.snackbarHostState.showSnackbar("Erreur : $errorMessage")
                                            }
                                        }
                                    )
                                }
                            },
                            showReportModal = { showReportModal = true }
                        )
                    }
                }
                is FicheObjetViewModel.ObjectState.Error -> {
                    Text(
                        text = (objectState as FicheObjetViewModel.ObjectState.Error).message,
                        color = Color.Red,
                        modifier = Modifier.align(Alignment.Center)
                    )
                }

                else -> {}
            }
        }
    }

    if (showReportModal) {
        ReportObjectModal(
            onDismiss = { showReportModal = false },
            onSubmit = { reportType, otherReason ->
                val finalReason = otherReason ?: reportType
                viewModel.reportObject(
                    objectId = objectId,
                    reportType = finalReason,
                    onSuccess = {
                        showReportModal = false
                        coroutineScope.launch {
                            scaffoldState.snackbarHostState.showSnackbar("Objet signalé avec succès")
                        }
                    },
                    onError = { errorMessage ->
                        showReportModal = false
                        coroutineScope.launch {
                            scaffoldState.snackbarHostState.showSnackbar("Erreur : $errorMessage")
                        }
                    }
                )
            },
            reportTypes = reportTypes,
            isLoading = isReporting
        )
    }
}

@Composable
fun ObjectDetail(
    obj: Object,
    navController: NavHostController,
    isLoading: Boolean,
    onToggleObjectStatus: () -> Unit,
    showReportModal: () -> Unit
) {
    val viewModel: FicheObjetViewModel = hiltViewModel()
    val user by viewModel.getCurrentUser().collectAsState(initial = null)
    var showQRModal by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        // Contenu du détail de l'objet
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Image(
                    painter = rememberImagePainter(data = obj.user?.profilePicture),
                    contentDescription = "User Profile Picture",
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape),
                    contentScale = ContentScale.Crop
                )
                Spacer(modifier = Modifier.width(8.dp))
                Column {
                    Text(
                        text = obj.user?.username ?: "",
                        style = MaterialTheme.typography.subtitle1,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
            Text(
                text = if (obj.status == "Available") "Disponible" else "Retiré",
                color = if (obj.status == "Available") Color(0xFF388E3C) else Color(0xFFD32F2F),
                style = MaterialTheme.typography.body2
            )
        }

        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
                .clip(RoundedCornerShape(16.dp)),
            backgroundColor = Color(0xFFF2F2F2),
            elevation = 4.dp
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .background(Color(0xFFF2F2F2))
            ) {
                Image(
                    painter = rememberImagePainter(data = obj.image),
                    contentDescription = "Object Image",
                    modifier = Modifier
                        .align(Alignment.Center)
                        .aspectRatio(1f)
                        .padding(16.dp)
                        .clip(RoundedCornerShape(16.dp)),
                    contentScale = ContentScale.Fit
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Nouvelle zone pour les icônes
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            // Icônes à gauche (mettre à jour et changer le statut)
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                if (user?.id == obj.user?.id) {
                    if (isLoading) {
                        CircularProgressIndicator(modifier = Modifier.size(48.dp))
                    } else {
                        IconButton(onClick = {
                            navController.navigate("editobject/${obj.id}")
                        }) {
                            Icon(
                                imageVector = Icons.Default.Edit,
                                contentDescription = "Mettre à jour",
                                tint = Color.Black
                            )
                        }
                        IconButton(
                            onClick = onToggleObjectStatus,
                            modifier = Modifier.size(48.dp)
                        ) {
                            Icon(
                                imageVector = if (obj.status == "Available") Icons.Default.Visibility else Icons.Default.VisibilityOff,
                                contentDescription = if (obj.status == "Available") "Retirer l'objet" else "Republier l'objet",
                                tint = Color.Red
                            )
                        }
                    }
                }
            }

            // Icônes à droite (partager et signaler)
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                if(obj.status == "Available"){
                    IconButton(onClick = { showQRModal = true }) {
                        Icon(
                            imageVector = Icons.Default.Share,
                            contentDescription = "Partager",
                            tint = Color.Black
                        )
                    }
                }
                IconButton(onClick = showReportModal) {
                    Icon(
                        imageVector = Icons.Default.Flag,
                        contentDescription = "Signaler",
                        tint = Color.Red
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = obj.category?.name ?: "N/A",
            style = MaterialTheme.typography.caption,
            color = Color.White,
            modifier = Modifier
                .background(Color(0xFFBC8246), shape = RoundedCornerShape(8.dp))
                .padding(horizontal = 8.dp, vertical = 4.dp)
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = obj.name,
            style = MaterialTheme.typography.h5,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(8.dp)
        )

        Text(
            text = obj.description ?: "Pas de description",
            style = MaterialTheme.typography.body1,
            modifier = Modifier.padding(8.dp)
        )

        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .padding(8.dp)
                .background(Color(0xFFD3D3D3), shape = RoundedCornerShape(8.dp))
                .padding(horizontal = 8.dp, vertical = 4.dp)
        ) {
            Icon(Icons.Default.CalendarToday, contentDescription = "Date Icon")
            Spacer(modifier = Modifier.width(4.dp))
            val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
            val formattedDate = dateFormat.format(obj.createdAt)
            Text(
                text = "publié le $formattedDate",
                style = MaterialTheme.typography.body2
            )
        }

        if (showQRModal) {
            ObjectQRModal(
                obj = obj,
                onDismiss = { showQRModal = false }
            )
        }
    }
}

@Composable
fun ReportObjectModal(
    onDismiss: () -> Unit,
    onSubmit: (reportType: String, otherReason: String?) -> Unit,
    reportTypes: List<String>,
    isLoading: Boolean
) {
    var selectedType by remember { mutableStateOf(reportTypes.firstOrNull() ?: "") }
    var expanded by remember { mutableStateOf(false) }
    var otherReason by remember { mutableStateOf("") }
    var showError by remember { mutableStateOf(false) }

    AlertDialog(
        onDismissRequest = { onDismiss() },
        title = { Text(text = "Signaler l'objet") },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp)
                    .heightIn(min = 200.dp)
            ) {
                Spacer(modifier = Modifier.height(16.dp))

                Box(modifier = Modifier.fillMaxWidth()) {
                    OutlinedTextField(
                        value = selectedType,
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Type de signalement") },
                        trailingIcon = {
                            Icon(
                                imageVector = if (expanded) Icons.Filled.ArrowDropUp else Icons.Filled.ArrowDropDown,
                                contentDescription = null
                            )
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { expanded = !expanded }
                            .padding(bottom = 12.dp),
                        enabled = !isLoading
                    )

                    DropdownMenu(
                        expanded = expanded,
                        onDismissRequest = { expanded = false }
                    ) {
                        reportTypes.forEach { type ->
                            DropdownMenuItem(onClick = {
                                selectedType = type
                                expanded = false
                                showError = false
                            }) {
                                Text(text = type)
                            }
                        }
                    }
                }

                if (selectedType == "Autre") {
                    Spacer(modifier = Modifier.height(12.dp))
                    OutlinedTextField(
                        value = otherReason,
                        onValueChange = {
                            otherReason = it
                            showError = false
                        },
                        label = { Text("Veuillez préciser") },
                        isError = showError && otherReason.isBlank(),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(80.dp),
                        enabled = !isLoading
                    )
                    if (showError && otherReason.isBlank()) {
                        Text(
                            text = "Ce champ est requis",
                            color = MaterialTheme.colors.error,
                            style = MaterialTheme.typography.caption
                        )
                    }
                }
            }
        },
        buttons = {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp)
            ) {
                Button(
                    onClick = onDismiss,
                    colors = ButtonDefaults.buttonColors(
                        backgroundColor = Color.Black,
                        contentColor = Color.White
                    )
                ) {
                    Text("Annuler")
                }

                Spacer(modifier = Modifier.weight(1f))

                Button(
                    onClick = {
                        if (selectedType == "Autre" && otherReason.isBlank()) {
                            showError = true
                        } else {
                            onSubmit(selectedType, if (selectedType == "Autre") otherReason else null)
                        }
                    },
                    enabled = !isLoading
                ) {
                    Text("Signaler")
                }
            }
        },
        modifier = Modifier
            .fillMaxWidth()
            .height(300.dp)
    )
}
