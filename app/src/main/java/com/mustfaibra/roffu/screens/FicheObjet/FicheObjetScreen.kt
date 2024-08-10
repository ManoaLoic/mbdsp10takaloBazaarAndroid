package com.mustfaibra.roffu.screens.ficheobjet

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Flag
import androidx.compose.material.icons.filled.Share
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
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
import com.mustfaibra.roffu.models.Object
import com.mustfaibra.roffu.sealed.Screen
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun FicheObjetScreen(navController: NavHostController, objectId: Int) {
    val viewModel: FicheObjetViewModel = hiltViewModel()
    val obj by viewModel.obj.collectAsState()

    LaunchedEffect(objectId) {
        viewModel.fetchObjectById(objectId)
    }

    Scaffold(
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
            obj?.let {
                Button(
                    onClick = {
                        navController.navigate("${Screen.ProposerEchange}/${it.id}")
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
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            obj?.let {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(bottom = 60.dp)
                ) {
                    item {
                        ObjectDetail(obj = it, navController = navController)
                    }
                }

                Button(
                    onClick = {
                        navController.navigate("exchange/propose/${it.user?.id}") {
                            popUpTo("home") { inclusive = true }
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .align(Alignment.BottomCenter)
                        .padding(16.dp),
                    colors = ButtonDefaults.buttonColors(backgroundColor = Color(0xFFBC8246))
                ) {
                    Text("Proposer un échange", color = Color.White)
                }
            } ?: run {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                Text("", modifier = Modifier.align(Alignment.Center))
            }
        }
    }
}

@Composable
fun ObjectDetail(obj: Object, navController: NavHostController) {
    val viewModel: FicheObjetViewModel = hiltViewModel()
    val user by viewModel.getCurrentUser().collectAsState(initial = null)

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
            if (obj.status == "Available") {
                Text(
                    text = "Disponible",
                    color = Color(0xFF388E3C),
                    style = MaterialTheme.typography.body2
                )
            } else {
                Text(
                    text = "Retiré",
                    color = Color(0xFFD32F2F),
                    style = MaterialTheme.typography.body2
                )
            }
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
            // Icônes à gauche (mettre à jour et supprimer)
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                if (user?.id == obj.user?.id) {
                    IconButton(onClick = { /* TODO: Action de mise à jour */ }) {
                        Icon(
                            imageVector = Icons.Default.Edit,
                            contentDescription = "Mettre à jour",
                            tint = Color.Gray
                        )
                    }
                    IconButton(onClick = { /* TODO: Action de suppression */ }) {
                        Icon(
                            imageVector = Icons.Default.Delete,
                            contentDescription = "Supprimer",
                            tint = Color.Red
                        )
                    }
                }
            }

            // Icônes à droite (partager et signaler)
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                IconButton(onClick = { /* TODO: Action de partage */ }) {
                    Icon(
                        imageVector = Icons.Default.Share,
                        contentDescription = "Partager",
                        tint = Color.Black
                    )
                }
                IconButton(onClick = { /* TODO: Action de signalement */ }) {
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
    }
}

