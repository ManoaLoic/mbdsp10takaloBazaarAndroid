package com.tpt.takalobazaar.screens.register

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.tpt.takalobazaar.R
import com.tpt.takalobazaar.components.CustomButton
import com.tpt.takalobazaar.components.CustomInputField
import com.tpt.takalobazaar.models.RegisterRequest
import com.tpt.takalobazaar.ui.theme.Dimension

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun RegisterScreen(
    navController: NavHostController,
    viewModel: RegisterViewModel = hiltViewModel(),
    onToastRequested: (message: String, color: Color) -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()
    val errorMessage by viewModel.errorMessage.collectAsState()

    var nom by remember { mutableStateOf("") }
    var prenom by remember { mutableStateOf("") }
    var pseudo by remember { mutableStateOf("") }
    var gender by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var showPassword by remember { mutableStateOf(false) }

    val genderOptions = listOf("Male" to "Homme", "Female" to "Femme")
    var expanded by remember { mutableStateOf(false) }

    LaunchedEffect(errorMessage) {
        errorMessage?.let { message ->
            onToastRequested(message, Color.Red)
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(Dimension.pagePadding)
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Image(
            painter = painterResource(id = R.drawable.logo_no_background),
            contentDescription = null,
            modifier = Modifier
                .size(200.dp)
                .clip(MaterialTheme.shapes.medium)
        )

        Spacer(modifier = Modifier.height(Dimension.pagePadding))

        Text(
            text = "Rejoignez la communité et commencez à échanger vos objets dès aujourd'hui !",
            fontSize = 16.sp,
            fontWeight = FontWeight.Medium,
            modifier = Modifier.padding(bottom = Dimension.pagePadding)
        )

        CustomInputField(
            modifier = Modifier
                .shadow(
                    elevation = Dimension.elevation,
                    shape = MaterialTheme.shapes.large,
                )
                .fillMaxWidth(),
            value = nom,
            onValueChange = { nom = it },
            onKeyboardActionClicked = {},
            onFocusChange = {},
            placeholder = "Nom",
            textStyle = MaterialTheme.typography.caption.copy(fontWeight = FontWeight.Medium),
            padding = PaddingValues(
                horizontal = Dimension.pagePadding,
                vertical = Dimension.pagePadding.times(0.7f),
            ),
            backgroundColor = MaterialTheme.colors.surface,
            textColor = MaterialTheme.colors.onBackground,
            imeAction = ImeAction.Next,
            shape = MaterialTheme.shapes.large,
            leadingIcon = {
                Icon(
                    painter = painterResource(id = R.drawable.ic_profile_empty),
                    contentDescription = null,
                    tint = MaterialTheme.colors.onBackground.copy(alpha = 0.4f)
                )
            }
        )

        Spacer(modifier = Modifier.height(Dimension.pagePadding))

        CustomInputField(
            modifier = Modifier
                .shadow(
                    elevation = Dimension.elevation,
                    shape = MaterialTheme.shapes.large,
                )
                .fillMaxWidth(),
            value = prenom,
            onValueChange = { prenom = it },
            onKeyboardActionClicked = {},
            onFocusChange = {},
            placeholder = "Prénom",
            textStyle = MaterialTheme.typography.caption.copy(fontWeight = FontWeight.Medium),
            padding = PaddingValues(
                horizontal = Dimension.pagePadding,
                vertical = Dimension.pagePadding.times(0.7f),
            ),
            backgroundColor = MaterialTheme.colors.surface,
            textColor = MaterialTheme.colors.onBackground,
            imeAction = ImeAction.Next,
            shape = MaterialTheme.shapes.large,
            leadingIcon = {
                Icon(
                    painter = painterResource(id = R.drawable.ic_profile_empty),
                    contentDescription = null,
                    tint = MaterialTheme.colors.onBackground.copy(alpha = 0.4f),
                )
            }
        )

        Spacer(modifier = Modifier.height(Dimension.pagePadding))

        CustomInputField(
            modifier = Modifier
                .shadow(
                    elevation = Dimension.elevation,
                    shape = MaterialTheme.shapes.large,
                )
                .fillMaxWidth(),
            value = pseudo,
            onValueChange = { pseudo = it },
            onKeyboardActionClicked = {},
            onFocusChange = {},
            placeholder = "Pseudo",
            textStyle = MaterialTheme.typography.caption.copy(fontWeight = FontWeight.Medium),
            padding = PaddingValues(
                horizontal = Dimension.pagePadding,
                vertical = Dimension.pagePadding.times(0.7f),
            ),
            backgroundColor = MaterialTheme.colors.surface,
            textColor = MaterialTheme.colors.onBackground,
            imeAction = ImeAction.Next,
            shape = MaterialTheme.shapes.large,
            leadingIcon = {
                Icon(
                    painter = painterResource(id = R.drawable.ic_profile_empty),
                    contentDescription = null,
                    tint = MaterialTheme.colors.onBackground.copy(alpha = 0.4f),
                )
            }
        )

        Spacer(modifier = Modifier.height(Dimension.pagePadding))

        // Gender Selection
        ExposedDropdownMenuBox(
            expanded = expanded,
            onExpandedChange = { expanded = !expanded }
        ) {
            TextField(
                value = genderOptions.firstOrNull { it.first == gender }?.second ?: "",
                onValueChange = {},
                readOnly = true,
                label = null,
                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                modifier = Modifier
                    .shadow(
                        elevation = Dimension.elevation,
                        shape = MaterialTheme.shapes.large,
                    )
                    .fillMaxWidth()
                    .background(MaterialTheme.colors.surface)
                    .clip(MaterialTheme.shapes.large)
                    .clickable { expanded = !expanded }
                    .padding(PaddingValues(horizontal = Dimension.pagePadding)),
                leadingIcon = {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_profile_empty),
                        contentDescription = null,
                        tint = MaterialTheme.colors.onBackground.copy(alpha = 0.4f),
                        modifier = Modifier.clickable { expanded = !expanded }
                    )
                },
                textStyle = MaterialTheme.typography.caption.copy(fontWeight = FontWeight.Medium),
                colors = TextFieldDefaults.textFieldColors(
                    backgroundColor = MaterialTheme.colors.surface,
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent,
                    disabledIndicatorColor = Color.Transparent
                )
            )

            ExposedDropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false }
            ) {
                genderOptions.forEach { (value, label) ->
                    DropdownMenuItem(onClick = {
                        gender = value
                        expanded = false
                    }) {
                        Text(text = label)
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(Dimension.pagePadding))

        CustomInputField(
            modifier = Modifier
                .shadow(
                    elevation = Dimension.elevation,
                    shape = MaterialTheme.shapes.large,
                )
                .fillMaxWidth(),
            value = email,
            onValueChange = { email = it },
            onKeyboardActionClicked = {},
            onFocusChange = {},
            placeholder = "Email",
            textStyle = MaterialTheme.typography.caption.copy(fontWeight = FontWeight.Medium),
            padding = PaddingValues(
                horizontal = Dimension.pagePadding,
                vertical = Dimension.pagePadding.times(0.7f),
            ),
            backgroundColor = MaterialTheme.colors.surface,
            textColor = MaterialTheme.colors.onBackground,
            imeAction = ImeAction.Next,
            shape = MaterialTheme.shapes.large,
            leadingIcon = {
                Icon(
                    painter = painterResource(id = R.drawable.ic_email),
                    contentDescription = null,
                    tint = MaterialTheme.colors.onBackground.copy(alpha = 0.4f),
                )
            }
        )

        Spacer(modifier = Modifier.height(Dimension.pagePadding))

        CustomInputField(
            modifier = Modifier
                .shadow(
                    elevation = Dimension.elevation,
                    shape = MaterialTheme.shapes.large,
                )
                .fillMaxWidth(),
            value = password,
            onValueChange = { password = it },
            onKeyboardActionClicked = {},
            onFocusChange = {},
            placeholder = "Mot de passe",
            textStyle = MaterialTheme.typography.caption.copy(fontWeight = FontWeight.Medium),
            padding = PaddingValues(
                horizontal = Dimension.pagePadding,
                vertical = Dimension.pagePadding.times(0.7f),
            ),
            backgroundColor = MaterialTheme.colors.surface,
            textColor = MaterialTheme.colors.onBackground,
            imeAction = ImeAction.Done,
            visualTransformation = if (showPassword) VisualTransformation.None else PasswordVisualTransformation(),
            leadingIcon = {
                Icon(
                    painter = painterResource(id = R.drawable.ic_lock),
                    contentDescription = null,
                    tint = MaterialTheme.colors.onBackground.copy(alpha = 0.4f),
                )
            },
            trailingIcon = {
                IconButton(
                    onClick = { showPassword = !showPassword },
                    modifier = Modifier.padding(end = Dimension.pagePadding.div(2))
                ) {
                    Icon(
                        imageVector = if (showPassword) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                        contentDescription = "Toggle Password Visibility"
                    )
                }
            }
        )

        Spacer(modifier = Modifier.height(Dimension.pagePadding))

        CustomButton(
            modifier = Modifier
                .fillMaxWidth()
                .shadow(
                    elevation = if (uiState !is UiState.Loading) Dimension.elevation else Dimension.zero,
                    shape = MaterialTheme.shapes.large,
                ),
            shape = MaterialTheme.shapes.large,
            padding = PaddingValues(Dimension.pagePadding.div(2)),
            buttonColor = MaterialTheme.colors.primary,
            contentColor = MaterialTheme.colors.onPrimary,
            text = stringResource(id = R.string.signup),
            enabled = uiState !is UiState.Loading,
            textStyle = MaterialTheme.typography.button,
            onButtonClicked = {
                if (nom.isNotBlank() && prenom.isNotBlank() && pseudo.isNotBlank() && gender.isNotBlank() && email.isNotBlank() && password.isNotBlank()) {
                    val newUser = RegisterRequest(
                        lastName = nom,
                        firstName = prenom,
                        username = pseudo,
                        gender = gender,
                        email = email,
                        password = password
                    )
                    viewModel.registerUser(
                        user = newUser,
                        onSuccess = {
                            navController.navigate("home") {
                                popUpTo("register") { inclusive = true }
                            }
                        },
                        onToastRequested = { message, color ->
                            onToastRequested(message, color)
                        }
                    )
                } else {
                    onToastRequested("Veuillez remplir tous les champs!", Color.Red)
                }
            },

            leadingIcon = {
                if (uiState is UiState.Loading) {
                    CircularProgressIndicator(
                        modifier = Modifier
                            .padding(end = Dimension.pagePadding)
                            .size(Dimension.smIcon),
                        color = MaterialTheme.colors.onPrimary,
                        strokeWidth = Dimension.xs
                    )
                }
            }
        )

        Spacer(modifier = Modifier.height(Dimension.pagePadding))

        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Divider(modifier = Modifier.weight(1f))
            Text(
                text = "OU",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(horizontal = 8.dp)
            )
            Divider(modifier = Modifier.weight(1f))
        }

        Spacer(modifier = Modifier.height(Dimension.pagePadding))

        CustomButton(
            modifier = Modifier
                .fillMaxWidth()
                .shadow(
                    elevation = Dimension.elevation,
                    shape = MaterialTheme.shapes.large,
                ),
            shape = MaterialTheme.shapes.large,
            padding = PaddingValues(Dimension.pagePadding.div(2)),
            buttonColor = Color.Black,  // Black background
            contentColor = Color.White, // White text
            text = "Se connecter",
            textStyle = MaterialTheme.typography.button,
            onButtonClicked = {
                navController.navigate("login") {
                    popUpTo("register") { inclusive = true }
                }
            }
        )
    }
}