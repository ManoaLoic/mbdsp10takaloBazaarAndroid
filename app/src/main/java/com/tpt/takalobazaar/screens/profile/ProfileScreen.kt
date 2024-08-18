package com.tpt.takalobazaar.screens.profile

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Logout
import androidx.compose.material.icons.rounded.KeyboardArrowRight
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.core.content.FileProvider
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import coil.compose.rememberAsyncImagePainter
import com.tpt.takalobazaar.R
import com.tpt.takalobazaar.components.DrawableButton
import com.tpt.takalobazaar.components.IconButton
import com.tpt.takalobazaar.models.LoginUser
import com.tpt.takalobazaar.sealed.Screen
import com.tpt.takalobazaar.ui.theme.Dimension
import java.io.File

@Composable
fun ProfileScreen(
    onNavigationRequested: (route: String, removePreviousRoute: Boolean) -> Unit,
    profileViewModel: ProfileViewModel = hiltViewModel(),
) {
    val isLoggingOut by rememberUpdatedState(profileViewModel.isLoggingOut.value)
    val isLoading by rememberUpdatedState(profileViewModel.isLoading.value)
    val userProfile by rememberUpdatedState(profileViewModel.userProfile.value)

    val generalOptions = remember {
        listOf(Screen.ExchangeHistory)
    }
    val personalOptions = remember {
        listOf(Screen.UpdateAccount)
    }
    val exchangeOptions = remember {
        listOf(Screen.CurrentExchange)
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(all = Dimension.pagePadding),
        verticalArrangement = Arrangement.spacedBy(Dimension.pagePadding),
    ) {
        item {
            Text(
                text = stringResource(id = R.string.your_profile),
                style = MaterialTheme.typography.button,
                color = MaterialTheme.colors.onBackground,
            )
        }
        /** Header section */
        item {
            if (isLoading) {
                Box(modifier = Modifier.fillMaxWidth()) {
                    CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                }
            } else {
                userProfile?.let { user ->
                    ProfileHeaderSection(
                        image = user.profilePicture,
                        name = "${user.firstName} ${user.lastName}",
                        email = user.email,
                        username = user.username,
                        onImageClicked = { uri ->
                            profileViewModel.updateUserProfilePicture(uri) {
                                // Redirect to refresh the page
                                onNavigationRequested(Screen.Profile.route, true)
                            }
                        }
                    )
                }
            }
        }

        /** Add virtual card section */
        /** My Objects section */
        item {
            Card(
                modifier = Modifier.clickable { onNavigationRequested(Screen.MyObjects.route, false) },
                shape = MaterialTheme.shapes.medium,
                backgroundColor = MaterialTheme.colors.primary,
                contentColor = MaterialTheme.colors.onSecondary,
            ) {
                Column(
                    modifier = Modifier.padding(Dimension.pagePadding),
                    verticalArrangement = Arrangement.spacedBy(Dimension.md),
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Text(
                            modifier = Modifier.weight(1f),
                            text = stringResource(id = R.string.my_objects),
                            style = MaterialTheme.typography.button,
                        )
                        IconButton(
                            icon = Icons.Rounded.KeyboardArrowRight,
                            backgroundColor = MaterialTheme.colors.background,
                            iconTint = MaterialTheme.colors.onBackground,
                            onButtonClicked = {},
                            iconSize = Dimension.smIcon,
                            paddingValue = PaddingValues(Dimension.xs),
                            shape = MaterialTheme.shapes.medium,
                        )
                    }
                    Text(
                        text = "Garder un oeil sur vos biens et gérer les facilement.",
                        style = MaterialTheme.typography.body2,
                    )
                }
            }
        }

        /** General options */
        item {
            Text(
                text = "Generale",
                style = MaterialTheme.typography.body1,
            )
        }
        items(exchangeOptions) { option ->
            ProfileOptionItem(
                icon = R.drawable.ic_exchange,
                title = R.string.current_exchanges,
                onOptionClicked = {
                    onNavigationRequested(option.route, false)
                },
            )
        }
        items(generalOptions) { option ->
            ProfileOptionItem(
                icon = option.icon,
                title = R.string.historique_exchange,
                onOptionClicked = {
                    onNavigationRequested(option.route, false)
                },
            )
        }
        item {
            ProfileOptionItem(
                icon = R.drawable.ic_logout,
                title = R.string.logOut,
                onOptionClicked = {
                    profileViewModel.logOut(
                        onLogoutSuccess = {
                            onNavigationRequested(Screen.Home.route, true)
                        },
                        onLogoutFailure = {
                            onNavigationRequested(Screen.Home.route, true)
                        }
                    )
                },
            )
        }

        /** Personal options */
        item {
            Text(
                text = "Informations personnelles",
                style = MaterialTheme.typography.body1,
            )
        }
        items(personalOptions) { option ->
            ProfileOptionItem(
                icon = option.icon,
                title = option.title,
                onOptionClicked = {
                    val userId = profileViewModel.userId!!
                    onNavigationRequested(Screen.ModifProfil.createRoute(userId), false)
                },
            )
        }
        item {
            ProfileOptionItem(
                icon = R.drawable.ic_terms,
                title = R.string.change_password,
                onOptionClicked = {
                    onNavigationRequested("changePassword/${profileViewModel.userId}", false)
                },
            )
        }

//        item {
//            Row(
//                modifier = Modifier
//                    .fillMaxWidth()
//                    .padding(vertical = Dimension.pagePadding)
//                    .background(MaterialTheme.colors.primary, shape = MaterialTheme.shapes.medium)
//                    .clickable {
//                        profileViewModel.logOut(
//                            onLogoutSuccess = {
//                                onNavigationRequested(Screen.Home.route, true)
//                            },
//                            onLogoutFailure = {
//                                onNavigationRequested(Screen.Home.route, true)
//                            }
//                        )
//                    },
//                horizontalArrangement = Arrangement.Center,
//                verticalAlignment = Alignment.CenterVertically
//            ) {
//                if (isLoggingOut) {
//                    CircularProgressIndicator(
//                        color = Color.White,
//                        modifier = Modifier
//                            .padding(vertical = Dimension.sm, horizontal = Dimension.lg)
//                            .size(24.dp)
//                    )
//                } else {
//                    Icon(
//                        imageVector = Icons.Rounded.KeyboardArrowRight,
//                        contentDescription = null,
//                        tint = Color.White,
//                        modifier = Modifier.padding(end = 8.dp)
//                    )
//                    Text(
//                        text = "Se déconnecter",
//                        style = MaterialTheme.typography.button,
//                        color = Color.White,
//                        modifier = Modifier
//                            .padding(vertical = Dimension.sm, horizontal = Dimension.lg)
//                    )
//                }
//            }
//        }
    }
}

@Composable
fun ProfileOptionItem(icon: Int?, title: Int?, onOptionClicked: () -> Unit) {
    Row(
        modifier = Modifier
            .clip(CircleShape)
            .fillMaxWidth()
            .clickable { onOptionClicked() },
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(Dimension.pagePadding),
    ) {
        DrawableButton(
            painter = rememberAsyncImagePainter(model = icon),
            backgroundColor = MaterialTheme.colors.primary.copy(alpha = 0.4f),
            iconTint = MaterialTheme.colors.primary,
            onButtonClicked = {},
            iconSize = Dimension.smIcon,
            paddingValue = PaddingValues(Dimension.md),
            shape = CircleShape,
        )
        title?.let {
            Text(
                text = stringResource(id = title),
                style = MaterialTheme.typography.body1,
                modifier = Modifier.weight(1f),
                color = MaterialTheme.colors.onBackground.copy(alpha = 0.7f),
            )
        }
        IconButton(
            icon = Icons.Rounded.KeyboardArrowRight,
            backgroundColor = MaterialTheme.colors.background,
            iconTint = MaterialTheme.colors.onBackground,
            onButtonClicked = {},
            iconSize = Dimension.smIcon,
            paddingValue = PaddingValues(Dimension.md),
            shape = CircleShape,
        )
    }
}

@Composable
fun ProfileHeaderSection(
    image: String?,
    name: String,
    email: String?,
    username: String?,
    onImageClicked: (Uri) -> Unit
) {
    val context = LocalContext.current

    // Create a URI for the image to be captured
    val capturedImageUri = remember {
        mutableStateOf<Uri?>(null)
    }

    // Camera Launcher
    val cameraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicture(),
        onResult = { success: Boolean ->
            if (success && capturedImageUri.value != null) {
                onImageClicked(capturedImageUri.value!!)
            }
        }
    )

    // Image Picker Launcher
    val imagePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent(),
        onResult = { uri: Uri? ->
            uri?.let { onImageClicked(it) }
        }
    )

    // Permission Request Launcher
    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions(),
        onResult = { permissions ->
            val allPermissionsGranted = permissions.entries.all { it.value == true }
            if (allPermissionsGranted) {
                // Create a temporary file to store the captured image
                val photoFile = File.createTempFile("temp_image", ".jpg", context.cacheDir)
                capturedImageUri.value = FileProvider.getUriForFile(
                    context,
                    "${context.packageName}.provider",
                    photoFile
                )
                cameraLauncher.launch(capturedImageUri.value)
            } else {
                // Handle the case where permissions are not granted
                // You can show a message or disable the camera option
            }
        }
    )

    // Dialog for choosing between camera and gallery
    var showDialog by remember { mutableStateOf(false) }

    if (showDialog) {
        AlertDialog(
            onDismissRequest = { showDialog = false },
            title = { Text(text = "Nouvelle photo de profil") },
            text = {
                Column {
                    Text(
                        text = "Caméra",
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                permissionLauncher.launch(
                                    arrayOf(
                                        android.Manifest.permission.CAMERA,
                                        android.Manifest.permission.WRITE_EXTERNAL_STORAGE
                                    )
                                )
                                showDialog = false
                            }
                            .padding(vertical = 16.dp),
                        style = MaterialTheme.typography.body1
                    )
                    Divider()
                    Text(
                        text = "Gallérie",
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                imagePickerLauncher.launch("image/*")
                                showDialog = false
                            }
                            .padding(vertical = 16.dp),
                        style = MaterialTheme.typography.body1
                    )
                }
            },
            buttons = {
                TextButton(
                    onClick = { showDialog = false }
                ) {
                    Text("Annuler")
                }
            }
        )
    }

    Row(
        modifier = Modifier
            .fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(Dimension.pagePadding),
    ) {
        AsyncImage(
            model = image,
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .size(Dimension.xlIcon)
                .clip(CircleShape)
                .clickable {
                    showDialog = true
                },
            placeholder = painterResource(R.drawable.ic_fallback_profile),
            error = painterResource(R.drawable.ic_fallback_profile),
        )

        Column {
            Text(
                text = name,
                style = MaterialTheme.typography.h5,
            )
            Text(
                text = username ?: "",
                style = MaterialTheme.typography.caption
                    .copy(fontWeight = FontWeight.Medium),
            )
            Text(
                text = email ?: "",
                style = MaterialTheme.typography.body1,
                color = MaterialTheme.colors.onBackground.copy(alpha = 0.7f),
            )
        }
    }
}