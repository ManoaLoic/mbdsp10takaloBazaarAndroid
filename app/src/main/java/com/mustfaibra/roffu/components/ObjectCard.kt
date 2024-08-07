package com.mustfaibra.roffu.components

import android.os.Bundle
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import coil.compose.rememberImagePainter
import coil.request.ImageRequest
import com.mustfaibra.roffu.models.Object

@Composable
fun ObjectCard(
    obj: Object,
    isRecent: Boolean,
    navController: NavHostController
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
            .shadow(8.dp, shape = RoundedCornerShape(8.dp))
            .clickable { navController.navigate("ficheobjet/${obj.id}") },
        backgroundColor = Color.White,
        elevation = 4.dp
    ) {
        Column(modifier = Modifier.padding(8.dp)) {
            Box(modifier = Modifier.padding(12.dp)) {
                Image(
                    painter = rememberImagePainter(
                        ImageRequest.Builder(LocalContext.current)
                            .data(obj.image)
                            .build()
                    ),
                    contentDescription = null,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(150.dp),
                    contentScale = ContentScale.Crop
                )
                if (isRecent) {
                    Box(
                        modifier = Modifier
                            .align(Alignment.TopStart)
                            .offset(x = (-14).dp, y = (-10).dp)
                    ) {
                        Text(
                            text = "Récent",
                            color = Color.Black,
                            modifier = Modifier
                                .clip(CircleShape)
                                .background(Color(0xFF7D7A69))
                                .padding(horizontal = 8.dp, vertical = 4.dp),
                            style = MaterialTheme.typography.subtitle2
                        )
                    }
                }
            }
            Spacer(modifier = Modifier.height(8.dp))
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.clickable { navController.navigate("userprofile/${obj.user?.id}") }
            ) {
                Image(
                    painter = rememberImagePainter(
                        ImageRequest.Builder(LocalContext.current)
                            .data(obj.user?.profilePicture)
                            .build()
                    ),
                    contentDescription = null,
                    modifier = Modifier
                        .size(32.dp)
                        .clip(CircleShape)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(text = obj.user?.username ?: "", style = MaterialTheme.typography.subtitle1)
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text(text = obj.name, style = MaterialTheme.typography.subtitle1)
            Text(text = obj.category?.name ?: "", style = MaterialTheme.typography.subtitle1, color = Color.Gray)
        }
    }
}
