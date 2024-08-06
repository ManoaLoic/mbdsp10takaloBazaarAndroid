package com.mustfaibra.roffu.screens.objectsearch

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Search
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.draw.shadow
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import coil.compose.rememberImagePainter
import com.mustfaibra.roffu.components.ObjectCard
import com.mustfaibra.roffu.models.Category
import com.mustfaibra.roffu.screens.category.CategoryViewModel
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@Composable
fun ObjectSearchScreen(
    navController: NavHostController,
    objectSearchViewModel: ObjectSearchViewModel = hiltViewModel(),
    searchQuery: String
) {
    val objects by objectSearchViewModel.objects.collectAsState()
    val isLoading by objectSearchViewModel.isLoading.collectAsState()
    val hasMorePages by objectSearchViewModel.hasMorePages.collectAsState()
    val categoryViewModel: CategoryViewModel = hiltViewModel()
    val categories by categoryViewModel.categories.collectAsState()

    LaunchedEffect(searchQuery) {
        objectSearchViewModel.name.value = searchQuery
        objectSearchViewModel.loadObjects(resetPage = true)
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

                if (isLoading && objects.isEmpty()) {
                    CircularProgressIndicator(modifier = Modifier.align(Alignment.CenterHorizontally))
                } else {
                    if (objects.isEmpty()) {
                        Text("Aucun objet trouvé", modifier = Modifier.align(Alignment.CenterHorizontally))
                    } else {
                        val gridState = rememberLazyGridState()

                        LazyVerticalGrid(
                            state = gridState,
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

                        // Infinite scroll
                        val coroutineScope = rememberCoroutineScope()
                        LaunchedEffect(gridState) {
                            coroutineScope.launch {
                                snapshotFlow { gridState.layoutInfo.visibleItemsInfo.lastOrNull() }
                                    .collectLatest { lastVisibleItem ->
                                        if (lastVisibleItem != null && lastVisibleItem.index == objects.size - 1 && hasMorePages && !isLoading) {
                                            objectSearchViewModel.loadNextPage()
                                        }
                                    }
                            }
                        }

                        if (isLoading) {
                            CircularProgressIndicator(modifier = Modifier.align(Alignment.CenterHorizontally))
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
    var orderMenuExpanded by remember { mutableStateOf(false) }
    var selectedOrder by remember { mutableStateOf("le plus récent") }

    Column(modifier = Modifier
        .fillMaxWidth()
        .padding(8.dp)
    ) {
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
                    value = viewModel.name.value,
                    onValueChange = { viewModel.name.value = it },
                    label = { Text("Nom") },
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = viewModel.description.value,
                    onValueChange = { viewModel.description.value = it },
                    label = { Text("Description") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(min = 100.dp),
                    maxLines = 5,
                    singleLine = false
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
                    value = viewModel.createdAtStart.value,
                    onValueChange = { viewModel.createdAtStart.value = it },
                    label = { Text("Date de création min") },
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = viewModel.createdAtEnd.value,
                    onValueChange = { viewModel.createdAtEnd.value = it },
                    label = { Text("Date de création max") },
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(8.dp))

                // Dropdown for order selection
                Box(modifier = Modifier.fillMaxWidth()) {
                    OutlinedTextField(
                        value = selectedOrder,
                        onValueChange = {},
                        label = { Text("Ordre") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { orderMenuExpanded = true },
                        readOnly = true,
                        trailingIcon = {
                            Icon(imageVector = Icons.Default.ArrowDropDown, contentDescription = "Dropdown", modifier = Modifier.clickable { orderMenuExpanded = true })
                        }
                    )
                    DropdownMenu(
                        expanded = orderMenuExpanded,
                        onDismissRequest = { orderMenuExpanded = false },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        DropdownMenuItem(onClick = {
                            selectedOrder = "le plus récent"
                            viewModel.loadObjects(resetPage = true)
                            orderMenuExpanded = false
                        }) {
                            Text(text = "le plus récent")
                        }
                        DropdownMenuItem(onClick = {
                            selectedOrder = "le plus ancien"
                            viewModel.sortBy = "ASC"
                            viewModel.loadObjects(resetPage = true)
                            orderMenuExpanded = false
                        }) {
                            Text(text = "le plus ancien")
                        }
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Button(onClick = { viewModel.loadObjects(resetPage = true) }) {
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
