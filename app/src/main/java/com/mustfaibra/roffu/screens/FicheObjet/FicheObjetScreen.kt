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
import com.mustfaibra.roffu.R

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
            modifier = Modifier.padding(bottom = 16.dp)
        ) {
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
                Spacer(modifier = Modifier.height(4.dp))
                if (obj.status == "Available") {
                    Text(
                        text = "Disponible",
                        color = Color.Green,
                        style = MaterialTheme.typography.body2
                    )
                } else {
                    Text(
                        text = "Retiré",
                        color = Color.Red,
                        style = MaterialTheme.typography.body2
                    )
                }
            }
        }

        Image(
            painter = rememberImagePainter(data = obj.image),
            contentDescription = "Object Image",
            modifier = Modifier
                .fillMaxWidth()
                .height(200.dp)
                .clip(shape = RoundedCornerShape(8.dp)),
            contentScale = ContentScale.Crop
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = obj.category?.name ?: "N/A",
            style = MaterialTheme.typography.caption,
            color = Color(0xFF7D7A69),
            modifier = Modifier
                .background(Color(0xFFF3F3F3), shape = RoundedCornerShape(8.dp))
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
            modifier = Modifier.padding(8.dp)
        ) {
            Icon(Icons.Default.CalendarToday, contentDescription = "Date Icon")
            Spacer(modifier = Modifier.width(4.dp))
            Text(
                text = "publié le ${obj.createdAt}",
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
