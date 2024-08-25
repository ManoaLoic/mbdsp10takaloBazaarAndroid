package com.tpt.takalobazaar.screens.modifierprofil

import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import android.provider.MediaStore
import android.util.Base64
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
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController

@Composable
fun ModifierProfilScreen(
    navController: NavHostController,
    modifierProfilViewModel: ModifierProfilViewModel = hiltViewModel(),
    userId: Int
) {
    val userResponse by modifierProfilViewModel.user.collectAsState()
    val user = userResponse?.user
    val isLoading by modifierProfilViewModel.isLoading.collectAsState()

    var pseudo by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var nom by remember { mutableStateOf("") }
    var prenom by remember { mutableStateOf("") }
    var selectedSex by remember { mutableStateOf("") }
    var expanded by remember { mutableStateOf(false) }
    var showErrors by remember { mutableStateOf(false) }

    val context = LocalContext.current
    val scaffoldState = rememberScaffoldState()

    LaunchedEffect(Unit) {
        modifierProfilViewModel.loadUserProfile(userId)
    }

    LaunchedEffect(user) {
        user?.let {
            pseudo = it.username ?: ""
            email = it.email ?: ""
            nom = it.lastName ?: ""
            prenom = it.firstName ?: ""
            selectedSex = it.gender ?: ""
        }
    }

    Scaffold(
        scaffoldState = scaffoldState
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp),
            contentAlignment = Alignment.Center
        ) {
            Column(
                modifier = Modifier
                    .verticalScroll(rememberScrollState())
                    .fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Icon(
                    imageVector = Icons.Default.Person,
                    contentDescription = "User Profile",
                    modifier = Modifier.size(60.dp)
                )

                Spacer(modifier = Modifier.height(16.dp))

                Text(text = "Modification de mon Profil", style = MaterialTheme.typography.h5)

                Spacer(modifier = Modifier.height(20.dp))

                OutlinedTextField(
                    value = nom,
                    onValueChange = { nom = it },
                    label = { Text("Nom") },
                    modifier = Modifier.fillMaxWidth(),
                    isError = showErrors && nom.isEmpty()
                )
                if (showErrors && nom.isEmpty()) {
                    Text("Nom est requis", color = Color.Red, style = MaterialTheme.typography.caption)
                }

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = prenom,
                    onValueChange = { prenom = it },
                    label = { Text("Prénom") },
                    modifier = Modifier.fillMaxWidth(),
                    isError = showErrors && prenom.isEmpty()
                )
                if (showErrors && prenom.isEmpty()) {
                    Text("Prénom est requis", color = Color.Red, style = MaterialTheme.typography.caption)
                }

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = pseudo,
                    onValueChange = { pseudo = it },
                    label = { Text("Pseudo") },
                    modifier = Modifier.fillMaxWidth(),
                    isError = showErrors && pseudo.isEmpty()
                )
                if (showErrors && pseudo.isEmpty()) {
                    Text("Pseudo est requis", color = Color.Red, style = MaterialTheme.typography.caption)
                }

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = email,
                    onValueChange = { email = it },
                    label = { Text("Adresse Email") },
                    modifier = Modifier.fillMaxWidth(),
                    isError = showErrors && email.isEmpty()
                )
                if (showErrors && email.isEmpty()) {
                    Text("Adresse Email est requise", color = Color.Red, style = MaterialTheme.typography.caption)
                }

                Spacer(modifier = Modifier.height(8.dp))

                Box(modifier = Modifier.fillMaxWidth()) {
                    OutlinedTextField(
                        value = if (selectedSex == "Male") "Homme" else "Femme",
                        onValueChange = { },
                        readOnly = true,
                        label = { Text("Sexe") },
                        trailingIcon = {
                            Icon(
                                imageVector = if (expanded) Icons.Filled.ArrowDropUp else Icons.Filled.ArrowDropDown,
                                contentDescription = null,
                                modifier = Modifier.clickable { expanded = !expanded }
                            )
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { expanded = !expanded },
                        isError = showErrors && selectedSex.isEmpty()
                    )

                    DropdownMenu(
                        expanded = expanded,
                        onDismissRequest = { expanded = false }
                    ) {
                        DropdownMenuItem(onClick = {
                            selectedSex = "Male"
                            expanded = false
                        }) {
                            Text("Homme")
                        }
                        DropdownMenuItem(onClick = {
                            selectedSex = "Female"
                            expanded = false
                        }) {
                            Text("Femme")
                        }
                    }
                }
                if (showErrors && selectedSex.isEmpty()) {
                    Text("Sexe est requis", color = Color.Red, style = MaterialTheme.typography.caption)
                }

                Spacer(modifier = Modifier.height(16.dp))

                Button(
                    onClick = {
                        showErrors = true
                        if (pseudo.isNotEmpty() && email.isNotEmpty() && nom.isNotEmpty() && prenom.isNotEmpty() && selectedSex.isNotEmpty()) {
                            modifierProfilViewModel.updateProfile(
                                id = user?.id ?: userId,
                                username = pseudo,
                                email = email,
                                first_name = prenom,
                                last_name = nom,
                                gender = selectedSex,
                                navController = navController
                            )
                        }
                    }
                ) {
                    if (isLoading) {
                        CircularProgressIndicator(color = Color.White)
                    } else {
                        Icon(imageVector = Icons.Default.Save, contentDescription = "Enregistrer les modifications")
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Enregistrer les modifications")
                    }
                }
            }
        }
    }
}
