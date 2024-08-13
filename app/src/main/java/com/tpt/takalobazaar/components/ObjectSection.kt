package com.tpt.takalobazaar.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController

@Composable
fun ObjectSection(
    title: String,
    objects: List<com.tpt.takalobazaar.models.Object>,
    navController: NavHostController,
    onAddObjectClick: () -> Unit,
    onRemoveObjectClick: (com.tpt.takalobazaar.models.Object) -> Unit
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = title,
            style = MaterialTheme.typography.h6,
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 8.dp, bottom = 8.dp)
                .background(MaterialTheme.colors.secondary)
                .padding(8.dp)
        )

        Row(
            modifier = Modifier
                .horizontalScroll(rememberScrollState())
                .padding(8.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            objects.forEach { obj ->
                Box(
                    modifier = Modifier
                        .width(LocalConfiguration.current.screenWidthDp.dp / 2 - 24.dp)
                        .padding(top = 16.dp) // Adjusting padding to make room for the close button
                ) {
                    ObjectCard(
                        obj = obj,
                        isRecent = false,
                        navController = navController,
                        disableNavigation = true
                    )
                    Box(
                        modifier = Modifier
                            .size(24.dp)
                            .align(Alignment.TopEnd)
                            .offset(x = (1).dp, y = (-5).dp)
                            .background(Color.Black, shape = CircleShape)
                            .clickable { onRemoveObjectClick(obj) }
                    ) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Remove",
                            tint = Color.White,
                            modifier = Modifier
                                .size(16.dp)
                                .align(Alignment.Center)
                        )
                    }
                }
            }
            Box(
                modifier = Modifier
                    .width(LocalConfiguration.current.screenWidthDp.dp / 2 - 24.dp),
                contentAlignment = Alignment.Center
            ) {
                AddObjectButton(onClick = onAddObjectClick)
            }
        }
    }
}
