package com.mustfaibra.roffu.screens.ficheobjet

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.CalendarToday
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
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp),
            verticalArrangement = Arrangement.Top,
            horizontalAlignment = Alignment.Start
        ) {
            obj?.let {
                ObjectDetail(obj = it, navController = navController)
            } ?: run {
                CircularProgressIndicator()
                Text("Chargement...")
            }
        }
    }
}

@Composable
fun ObjectDetail(obj: Object, navController: NavHostController) {
    Column(
        modifier = Modifier.fillMaxSize()
    ) {
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
                    color = Color(0xFF388E3C), // Vert foncé
                    style = MaterialTheme.typography.body2
                )
            } else {
                Text(
                    text = "Retiré",
                    color = Color(0xFFD32F2F), // Rouge foncé
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
                // Ajouter les icônes de partage et de signalement en bas à droite
                Row(
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .padding(8.dp),
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
                .background(Color(0xFFD3D3D3), shape = RoundedCornerShape(8.dp)) // Gris plus clair
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

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = {
                navController.navigate("exchange/propose/${obj.user?.id}") {
                    popUpTo("home") { inclusive = true }
                }
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

fun getStatusLabel(status: String): String {
    return when (status) {
        "Available" -> "Disponible"
        "Removed" -> "Retiré"
        else -> status
    }
}