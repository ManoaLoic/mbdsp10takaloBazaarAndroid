package com.tpt.takalobazaar.screens.editobject

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
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
import com.tpt.takalobazaar.models.Category
import com.tpt.takalobazaar.screens.ajoutobjet.uriFromBitmap
import com.tpt.takalobazaar.screens.category.CategoryViewModel

@Composable
fun EditObjectScreen(
    navController: NavHostController,
    objectId: Int,
    editObjectViewModel: EditObjectViewModel = hiltViewModel(),
    categoryViewModel: CategoryViewModel = hiltViewModel()
) {
    val objectData by editObjectViewModel.objectData.collectAsState()
    val categories by categoryViewModel.categories.collectAsState()
    val isLoadingCategories by categoryViewModel.isLoading.collectAsState()
    val isLoadingSubmit by editObjectViewModel.isLoading.collectAsState()
    var name by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var selectedCategory by remember { mutableStateOf<Category?>(null) }
    var selectedImageUri by remember { mutableStateOf<Uri?>(null) }
    var showErrors by remember { mutableStateOf(false) }
    var expanded by remember { mutableStateOf(false) }
    val context = LocalContext.current
    val scaffoldState = rememberScaffoldState()

    val galleryLauncher = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
        selectedImageUri = uri
    }

    val cameraLauncher = rememberLauncherForActivityResult(ActivityResultContracts.TakePicturePreview()) { bitmap ->
        selectedImageUri = bitmap?.let { uriFromBitmap(context, it) }
    }

    LaunchedEffect(objectId) {
        editObjectViewModel.fetchObjectById(objectId)
    }

    LaunchedEffect(objectData) {
        objectData?.let { obj ->
            name = obj.name
            description = obj.description ?: ""
            selectedCategory = obj.category
            if (selectedImageUri == null) {
                selectedImageUri = Uri.parse(obj.image)
            }
        }
    }

    LaunchedEffect(Unit) {
        editObjectViewModel.toastMessage.collect { message ->
            message?.let {
                scaffoldState.snackbarHostState.showSnackbar(it)
                editObjectViewModel.clearToastMessage()
            }
        }
    }

    Scaffold(
        scaffoldState = scaffoldState
    ) { paddingValues ->
        Spacer(modifier = Modifier.height(100.dp))
        Column(
            modifier = Modifier
                .verticalScroll(rememberScrollState())
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(text = "Modifier l'objet", style = MaterialTheme.typography.h5)

            if (isLoadingCategories || isLoadingSubmit) {
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
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(min = 100.dp),
                    isError = showErrors && description.isEmpty(),
                    maxLines = 5,
                    singleLine = false
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
                Image(
                    painter = rememberImagePainter(
                        ImageRequest.Builder(context)
                            .data(selectedImageUri)
                            .build()
                    ),
                    contentDescription = null,
                    modifier = Modifier
                        .size(150.dp)
                        .padding(8.dp)
                )

                Row {
                    IconButton(onClick = { galleryLauncher.launch("image/*") }) {
                        Icon(imageVector = Icons.Rounded.Photo, contentDescription = "Choisir de la galerie")
                    }
                    IconButton(onClick = { cameraLauncher.launch(null) }) {
                        Icon(imageVector = Icons.Rounded.PhotoCamera, contentDescription = "Prendre une photo")
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))
                Row {
                    Button(
                        onClick = {
                            showErrors = true
                            if (name.isNotEmpty() && description.isNotEmpty() && selectedCategory != null) {
                                editObjectViewModel.updateObject(objectId, name, description, selectedCategory!!.id, selectedImageUri, navController)
                            }
                        }
                    ) {
                        if (isLoadingSubmit) {
                            CircularProgressIndicator(color = Color.White)
                        } else {
                            Icon(imageVector = Icons.Default.Edit, contentDescription = "Mettre à jour")
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Mettre à jour")
                        }
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    OutlinedButton(onClick = { navController.popBackStack() }) {
                        Icon(imageVector = Icons.Default.Close, contentDescription = "Annuler")
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Annuler")
                    }
                }
            }
        }
    }
}
