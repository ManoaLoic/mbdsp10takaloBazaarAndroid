package com.tpt.takalobazaar.screens.ChangePassword

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.tpt.takalobazaar.api.UserService
import com.tpt.takalobazaar.models.UpdateUserRequest
import com.tpt.takalobazaar.ui.theme.Dimension
import kotlinx.coroutines.launch
import javax.inject.Inject

@Composable
fun ChangePasswordScreen(
    userId: Int,
    navController: NavHostController,
    changePasswordViewModel: ChangePasswordViewModel = hiltViewModel()
) {
    var oldPassword by remember { mutableStateOf(TextFieldValue("")) }
    var newPassword by remember { mutableStateOf(TextFieldValue("")) }
    var confirmPassword by remember { mutableStateOf(TextFieldValue("")) }
    val isLoading by changePasswordViewModel.isLoading.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(Dimension.pagePadding),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(text = "Changer de mot de passe", style = MaterialTheme.typography.h5)

        Spacer(modifier = Modifier.height(20.dp))

        OutlinedTextField(
            value = oldPassword,
            onValueChange = { oldPassword = it },
            label = { Text("Ancien mot de passe") },
            visualTransformation = PasswordVisualTransformation(),
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(20.dp))

        OutlinedTextField(
            value = newPassword,
            onValueChange = { newPassword = it },
            label = { Text("Nouveau mot de passe") },
            visualTransformation = PasswordVisualTransformation(),
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(20.dp))

        OutlinedTextField(
            value = confirmPassword,
            onValueChange = { confirmPassword = it },
            label = { Text("Confirmer le mot de passe") },
            visualTransformation = PasswordVisualTransformation(),
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(30.dp))

        Button(
            onClick = {
                changePasswordViewModel.changePassword(
                    userId = userId,
                    oldPassword = oldPassword.text,
                    newPassword = newPassword.text,
                    confirmPassword = confirmPassword.text,
                    onSuccess = {
                        navController.popBackStack()
                        Toast.makeText(navController.context, "Mot de passe mis à jour avec succès", Toast.LENGTH_LONG).show()
                    },
                    onError = { message ->
                        Toast.makeText(navController.context, message, Toast.LENGTH_LONG).show()
                    }
                )
            },
            enabled = !isLoading,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(text = if (isLoading) "Mise à jour..." else "Mettre à jour")
        }
    }
}

