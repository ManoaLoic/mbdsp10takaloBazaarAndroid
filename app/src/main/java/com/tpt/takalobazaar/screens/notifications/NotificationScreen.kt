package com.tpt.takalobazaar.screens.notifications

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.tpt.takalobazaar.R
import com.tpt.takalobazaar.sealed.UiState
import com.tpt.takalobazaar.ui.theme.Dimension

@Composable
fun NotificationScreen(
    notificationViewModel: NotificationViewModel = hiltViewModel(),
    navController: NavController
) {
//    val notificationState by remember { notificationViewModel.notificationState }
//
//    if (notificationState.isVisible) {
//        Box(
//            modifier = Modifier
//                .fillMaxSize()
//                .padding(top = 10.dp)
//                .wrapContentHeight(Alignment.Top)
//                .shadow(elevation = 8.dp, shape = RoundedCornerShape(8.dp))
//                .background(MaterialTheme.colors.surface, shape = RoundedCornerShape(8.dp))
//                .padding(Dimension.pagePadding)
//        ) {
//            Column(
//                modifier = Modifier
//                    .fillMaxWidth()
//                    .padding(16.dp), // Adjust padding as needed
//                verticalArrangement = Arrangement.spacedBy(8.dp) // Space between items
//            ) {
//                Text(
//                    text = notificationState.title,
//                    style = MaterialTheme.typography.button,
//                    color = MaterialTheme.colors.onBackground,
//                )
//                Text(
//                    text = notificationState.message,
//                    style = MaterialTheme.typography.body1,
//                    color = MaterialTheme.colors.onBackground,
//                )
//
//                Spacer(modifier = Modifier.height(8.dp)) // Add space between the text and the button
//
//                Row(
//                    modifier = Modifier.fillMaxWidth(),
//                    horizontalArrangement = Arrangement.End // Align button to the right
//                ) {
//                    Button(
//                        onClick = {
//                            notificationViewModel.hideNotification()
//                            navController.navigate("currentExchange")
//                        },
//                        colors = ButtonDefaults.textButtonColors(
//                            backgroundColor = MaterialTheme.colors.primary,
//                            contentColor = Color.White
//                        ),
//                        modifier = Modifier.shadow(4.dp, RoundedCornerShape(8.dp))
//                    ) {
//                        Text("Voir")
//                    }
//                }
//            }
//        }
//    }
}