package com.mustfaibra.roffu.screens.objectsearch

import android.content.Context
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import coil.compose.rememberImagePainter
import coil.request.ImageRequest
import com.mustfaibra.roffu.components.ObjectCard
import com.mustfaibra.roffu.models.Category
import kotlinx.coroutines.launch
import com.mustfaibra.roffu.models.Object
import com.mustfaibra.roffu.screens.category.CategoryViewModel

@Composable
fun ObjectSearchScreen(
    navController: NavHostController,
    objectSearchViewModel: ObjectSearchViewModel = hiltViewModel(),
    searchQuery: String
) {
    val objects by objectSearchViewModel.objects.collectAsState()
    val isLoading by objectSearchViewModel.isLoading.collectAsState()
    val categoryViewModel: CategoryViewModel = hiltViewModel()
    val categories by categoryViewModel.categories.collectAsState()

    LaunchedEffect(searchQuery) {
        objectSearchViewModel.name = searchQuery
        objectSearchViewModel.loadObjects()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Liste des objets", style = MaterialTheme.typography.h5.copy(fontWeight = FontWeight.Bold, color = Color.Black)) },
                backgroundColor = Color.White,
                contentColor = Color.Black
            )
        },
        content = { paddingValues ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(16.dp)
            ) {
                AccordionFilter(objectSearchViewModel, categories)

                Spacer(modifier = Modifier.height(16.dp))

                if (isLoading) {
                    CircularProgressIndicator(modifier = Modifier.align(Alignment.CenterHorizontally))
                } else {
                    if (objects.isEmpty()) {
                        Text("Aucun objet trouvé", modifier = Modifier.align(Alignment.CenterHorizontally))
                    } else {
                        LazyVerticalGrid(
                            columns = GridCells.Fixed(2),
                            contentPadding = PaddingValues(4.dp),
                            modifier = Modifier.fillMaxSize(),
                            verticalArrangement = Arrangement.spacedBy(4.dp),
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            items(objects) { obj ->
                                ObjectCard(obj, false, navController)
                            }
                        }
                    }
                }
            }
        }
    )
}

@Composable
fun AccordionFilter(viewModel: ObjectSearchViewModel, categories: List<Category>) {
    var expanded by remember { mutableStateOf(true) }
    var menuExpanded by remember { mutableStateOf(false) }
    var selectedCategory by remember { mutableStateOf<Category?>(null) }

    Column {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { expanded = !expanded }
                .padding(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(imageVector = Icons.Default.FilterList, contentDescription = "Filtres")
            Spacer(modifier = Modifier.width(8.dp))
            Text("Filtres", style = MaterialTheme.typography.h5.copy(fontWeight = FontWeight.Bold))
            Spacer(modifier = Modifier.weight(1f))
            Icon(imageVector = if (expanded) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown, contentDescription = null)
        }

        if (expanded) {
            Column(modifier = Modifier.padding(8.dp)) {
                OutlinedTextField(
                    value = viewModel.name,
                    onValueChange = { viewModel.name = it },
                    label = { Text("Nom") },
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = viewModel.description,
                    onValueChange = { viewModel.description = it },
                    label = { Text("Description") },
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(8.dp))

                // Dropdown for category selection
                Box(modifier = Modifier.fillMaxWidth()) {
                    OutlinedTextField(
                        value = selectedCategory?.name ?: "Sélectionner une catégorie",
                        onValueChange = {},
                        label = { Text("Catégorie") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { menuExpanded = true },
                        readOnly = true,
                        trailingIcon = {
                            Icon(imageVector = Icons.Default.ArrowDropDown, contentDescription = "Dropdown", modifier = Modifier.clickable { menuExpanded = true })
                        }
                    )
                    DropdownMenu(
                        expanded = menuExpanded,
                        onDismissRequest = { menuExpanded = false },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        categories.forEach { category ->
                            DropdownMenuItem(onClick = {
                                viewModel.categoryId = category.id
                                selectedCategory = category
                                menuExpanded = false
                            }) {
                                Text(text = category.name)
                            }
                        }
                    }
                }

                selectedCategory?.let {
                    Text("Catégorie sélectionnée: ${it.name}", style = MaterialTheme.typography.body2)
                }

                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = viewModel.createdAtStart,
                    onValueChange = { viewModel.createdAtStart = it },
                    label = { Text("Date de début") },
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = viewModel.createdAtEnd,
                    onValueChange = { viewModel.createdAtEnd = it },
                    label = { Text("Date de fin") },
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Button(onClick = { viewModel.loadObjects() }) {
                        Icon(imageVector = Icons.Default.Search, contentDescription = "Filtrer")
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Filtrer")
                    }
                    Button(
                        onClick = { viewModel.resetFilters() },
                        colors = ButtonDefaults.buttonColors(backgroundColor = Color.Gray)
                    ) {
                        Icon(imageVector = Icons.Default.Refresh, contentDescription = "Réinitialiser")
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Réinitialiser", color = Color.White)
                    }
                }
            }
        }
    }
}