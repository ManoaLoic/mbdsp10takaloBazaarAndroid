package com.mustfaibra.roffu.screens.ajoutobjet

import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import android.provider.MediaStore
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.rounded.Photo
import androidx.compose.material.icons.rounded.PhotoCamera
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import coil.compose.rememberImagePainter
import coil.request.ImageRequest
import coil.transform.CircleCropTransformation
import com.mustfaibra.roffu.models.Category
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import java.io.ByteArrayOutputStream

@Composable
fun AjoutObjetScreen(
    navController: NavHostController,
    ajoutObjetViewModel: AjoutObjetViewModel = hiltViewModel(),
    categoryViewModel: CategoryViewModel = hiltViewModel()
) {
    val categories by categoryViewModel.categories.collectAsState()
    val isLoadingCategories by categoryViewModel.isLoading.collectAsState()
    val isLoadingSubmit by ajoutObjetViewModel.isLoading.collectAsState()
    var name by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var selectedCategory by remember { mutableStateOf<Category?>(null) }
    var expanded by remember { mutableStateOf(false) }
    var selectedImageUri by remember { mutableStateOf<Uri?>(null) }
    var showErrors by remember { mutableStateOf(false) }
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    val scaffoldState = rememberScaffoldState()

    val galleryLauncher = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
        selectedImageUri = uri
    }

    val cameraLauncher = rememberLauncherForActivityResult(ActivityResultContracts.TakePicturePreview()) { bitmap ->
        selectedImageUri = bitmap?.let { uriFromBitmap(context, it) }
    }

    LaunchedEffect(Unit) {
        ajoutObjetViewModel.toastMessage.collect { message ->
            message?.let {
                scaffoldState.snackbarHostState.showSnackbar(it)
                ajoutObjetViewModel.clearToastMessage()
            }
        }
    }

    Scaffold(
        scaffoldState = scaffoldState
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(text = "Nouvel Object", style = MaterialTheme.typography.h5, modifier = Modifier.padding(bottom = 16.dp))

            if (isLoadingCategories) {
                CircularProgressIndicator()
            } else {
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Nom") },
                    modifier = Modifier.fillMaxWidth(),
                    isError = showErrors && name.isEmpty()
                )
                if (showErrors && name.isEmpty()) {
                    Text("Nom est requis", color = Color.Red, style = MaterialTheme.typography.caption)
                }
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = description,
                    onValueChange = { description = it },
                    label = { Text("Description") },
                    modifier = Modifier.fillMaxWidth(),
                    isError = showErrors && description.isEmpty()
                )
                if (showErrors && description.isEmpty()) {
                    Text("Description est requis", color = Color.Red, style = MaterialTheme.typography.caption)
                }
                Spacer(modifier = Modifier.height(8.dp))

                // Dropdown for categories
                Box(modifier = Modifier.fillMaxWidth()) {
                    OutlinedTextField(
                        value = selectedCategory?.name ?: "Sélectionner une catégorie",
                        onValueChange = {},
                        label = { Text("Catégorie") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { expanded = !expanded },
                        readOnly = true,
                        trailingIcon = {
                            Icon(imageVector = Icons.Default.ArrowDropDown, contentDescription = "Dropdown", modifier = Modifier.clickable { expanded = !expanded })
                        },
                        isError = showErrors && selectedCategory == null
                    )
                    DropdownMenu(
                        expanded = expanded,
                        onDismissRequest = { expanded = false }
                    ) {
                        categories.forEach { category ->
                            DropdownMenuItem(onClick = {
                                selectedCategory = category
                                expanded = false
                                Log.d("AjoutObjetScreen", "Selected category: $category")
                            }) {
                                Text(text = category.name)
                            }
                        }
                    }
                    if (showErrors && selectedCategory == null) {
                        Text("Catégorie est requis", color = Color.Red, style = MaterialTheme.typography.caption)
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Image selection and preview
                if (selectedImageUri != null) {
                    Image(
                        painter = rememberImagePainter(
                            ImageRequest.Builder(context)
                                .data(selectedImageUri)
                                .transformations(CircleCropTransformation())
                                .build()
                        ),
                        contentDescription = null,
                        modifier = Modifier
                            .size(150.dp)
                            .padding(8.dp)
                    )
                    IconButton(onClick = { selectedImageUri = null }) {
                        Icon(imageVector = Icons.Default.Delete, contentDescription = "Supprimer l'image")
                    }
                } else {
                    if (showErrors) {
                        Text("Image est requis", color = Color.Red, style = MaterialTheme.typography.caption)
                    }
                }

                Row {
                    IconButton(onClick = { galleryLauncher.launch("image/*") }) {
                        Icon(imageVector = Icons.Rounded.Photo, contentDescription = "Choisir de la galerie")
                    }
                    IconButton(onClick = { cameraLauncher.launch(null) }) {
                        Icon(imageVector = Icons.Rounded.PhotoCamera, contentDescription = "Prendre une photo")
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))
                Button(
                    onClick = {
                        showErrors = true
                        if (name.isNotEmpty() && description.isNotEmpty() && selectedCategory != null && selectedImageUri != null) {
                            ajoutObjetViewModel.createObject(name, description, selectedCategory!!.id, selectedImageUri, context, navController)
                        }
                    }
                ) {
                    if (isLoadingSubmit) {
                        CircularProgressIndicator(color = Color.White)
                    } else {
                        Text("Créer l'objet")
                    }
                }
            }
        }
    }
}

// Helper function to convert bitmap to Uri
fun uriFromBitmap(context: Context, bitmap: Bitmap): Uri {
    val bytes = ByteArrayOutputStream()
    bitmap.compress(Bitmap.CompressFormat.JPEG, 100, bytes)
    val path = MediaStore.Images.Media.insertImage(context.contentResolver, bitmap, "Temp", null)
    return Uri.parse(path)
}
