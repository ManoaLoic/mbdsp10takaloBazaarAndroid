package com.mustfaibra.roffu.screens.ajoutobjet

import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import android.provider.MediaStore
import android.util.Base64
import android.util.Log
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
import androidx.compose.material.icons.rounded.Drafts
import androidx.compose.material.icons.rounded.Photo
import androidx.compose.material.icons.rounded.PhotoCamera
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import coil.compose.rememberImagePainter
import coil.request.ImageRequest
import com.mustfaibra.roffu.models.Category
import com.mustfaibra.roffu.screens.category.CategoryViewModel
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
    val drafts by ajoutObjetViewModel.drafts.collectAsState()
    var name by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var selectedCategory by remember { mutableStateOf<Category?>(null) }
    var expanded by remember { mutableStateOf(false) }
    var selectedImageUri by remember { mutableStateOf<Uri?>(null) }
    var showErrors by remember { mutableStateOf(false) }
    var showDraftsModal by remember { mutableStateOf(false) }
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
                .verticalScroll(rememberScrollState())
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Button(onClick = {
                    showDraftsModal = true
                    ajoutObjetViewModel.loadDrafts()
                }) {
                    Icon(imageVector = Icons.Rounded.Drafts, contentDescription = "Charger Brouillon")
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Brouillons")
                }
                Spacer(modifier = Modifier.width(8.dp))
                Button(onClick = {
                    if (name.isNotEmpty() && description.isNotEmpty() && selectedCategory != null && selectedImageUri != null) {
                        ajoutObjetViewModel.saveDraft(name, description, selectedCategory!!.id, selectedImageUri!!)
                    } else {
                        showErrors = true
                    }
                }) {
                    Icon(imageVector = Icons.Filled.Save, contentDescription = "Enregistrer Brouillon")
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Enregistrer")
                }
            }

            Text(text = "Nouvel Objet", style = MaterialTheme.typography.h5)

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
                Box(modifier =
                    Modifier
                        .clickable { expanded = !expanded }
                        .fillMaxWidth()
                ) {
                    OutlinedTextField(
                        value = selectedCategory?.name ?: "Sélectionner une catégorie",
                        onValueChange = {},
                        label = { Text("Catégorie") },
                        modifier = Modifier
                            .fillMaxWidth(),
                        readOnly = true,
                        trailingIcon = {
                            Icon(imageVector = Icons.Default.ArrowDropDown, contentDescription = "Dropdown", modifier = Modifier.clickable { expanded = !expanded })
                        },
                        isError = showErrors && selectedCategory == null,
                    )
                    DropdownMenu(
                        expanded = expanded,
                        onDismissRequest = { expanded = false },
                        modifier = Modifier
                                .clickable { expanded = !expanded }
                    ) {
                        categories.forEach { category ->
                            DropdownMenuItem(onClick = {
                                selectedCategory = category
                                expanded = !expanded
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
                            ajoutObjetViewModel.createObject(name, description, selectedCategory!!.id, selectedImageUri, navController)
                        }
                    }
                ) {
                    if (isLoadingSubmit) {
                        CircularProgressIndicator(color = Color.White)
                    } else {
                        Icon(imageVector = Icons.Default.Add, contentDescription = "Créer l'objet")
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Créer l'objet")
                    }
                }
            }

            if (showDraftsModal) {
                CustomAlertDialog(
                    onDismissRequest = { showDraftsModal = false },
                    title = {
                        Text(
                            text = "Charger Brouillon",
                            style = MaterialTheme.typography.h6.copy(
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colors.primary
                            )
                        )
                    },
                    content = {
                        Column(
                            modifier = Modifier
                                .fillMaxHeight(0.8f) // Set height for the entire dialog
                                .verticalScroll(rememberScrollState())
                        ) {
                            drafts.forEach { draft ->
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clickable {
                                            name = draft.name
                                            description = draft.description
                                            selectedCategory = categories.firstOrNull { it.id == draft.categoryId }
                                            selectedImageUri = Uri.parse(draft.imageUri)
                                            showDraftsModal = false
                                        }
                                        .padding(8.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Column {
                                        Text(draft.name, style = MaterialTheme.typography.body1)
                                        Text(categories.firstOrNull { it.id == draft.categoryId }?.name ?: "Catégorie inconnue", style = MaterialTheme.typography.body2)
                                    }
                                    IconButton(onClick = { ajoutObjetViewModel.deleteDraft(draft.id) }) {
                                        Icon(imageVector = Icons.Default.Delete, contentDescription = "Supprimer le brouillon")
                                    }
                                }
                                Divider()
                            }
                        }
                    },
                    confirmButton = {
                        Button(onClick = { showDraftsModal = false }) {
                            Text("Fermer")
                        }
                    }
                )
            }
        }
    }
}

@Composable
fun CustomAlertDialog(
    onDismissRequest: () -> Unit,
    title: @Composable () -> Unit,
    content: @Composable () -> Unit,
    confirmButton: @Composable () -> Unit
) {
    Dialog(onDismissRequest = onDismissRequest) {
        Surface(
            shape = MaterialTheme.shapes.medium,
            elevation = 24.dp
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                title()
                Spacer(modifier = Modifier.height(8.dp))
                content()
                Spacer(modifier = Modifier.height(16.dp))
                Row(
                    modifier = Modifier.align(Alignment.End),
                    horizontalArrangement = Arrangement.End
                ) {
                    confirmButton()
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
