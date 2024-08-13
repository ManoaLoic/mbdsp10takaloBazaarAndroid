package com.tpt.takalobazaar.screens.login

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Divider
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.tpt.takalobazaar.R
import com.tpt.takalobazaar.components.CustomButton
import com.tpt.takalobazaar.components.CustomInputField
import com.tpt.takalobazaar.sealed.UiState
import com.tpt.takalobazaar.ui.theme.Dimension

@Composable
fun LoginScreen(
    loginViewModel: LoginViewModel = hiltViewModel(),
    navController: NavHostController,
    onUserAuthenticated: () -> Unit,
    onToastRequested: (message: String, color: Color) -> Unit,
) {
    val uiState by remember { loginViewModel.uiState }
    val emailOrPhone by remember { loginViewModel.emailOrPhone }
    val password by remember { loginViewModel.password }
    val errorMessage by remember { loginViewModel.errorMessage }

    LaunchedEffect(errorMessage) {
        errorMessage?.let { message ->
            onToastRequested(message, Color.Red)
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(Dimension.pagePadding),
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

        /** Login info input section */
        CustomInputField(
            modifier = Modifier
                .shadow(
                    elevation = Dimension.elevation,
                    shape = MaterialTheme.shapes.large,
                )
                .fillMaxWidth(),
            value = emailOrPhone ?: "",
            onValueChange = {
                loginViewModel.updateEmailOrPhone(value = it.ifBlank { null })
            },
            placeholder = "Email ou nom d'utilisateur ...",
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
                    modifier = Modifier
                        .padding(end = Dimension.pagePadding.div(2))
                        .size(Dimension.mdIcon.times(0.7f)),
                    painter = painterResource(id = R.drawable.ic_profile_empty),
                    contentDescription = null,
                    tint = MaterialTheme.colors.onBackground.copy(alpha = 0.4f),
                )
            },
            onFocusChange = { },
            onKeyboardActionClicked = { },
        )
        Spacer(modifier = Modifier.height(Dimension.pagePadding))
        CustomInputField(
            modifier = Modifier
                .shadow(
                    elevation = Dimension.elevation,
                    shape = MaterialTheme.shapes.large,
                )
                .fillMaxWidth(),
            value = password ?: "",
            onValueChange = {
                loginViewModel.updatePassword(value = it.ifBlank { null })
            },
            placeholder = "Mot de passe ...",
            visualTransformation = PasswordVisualTransformation(),
            textStyle = MaterialTheme.typography.caption.copy(fontWeight = FontWeight.Medium),
            padding = PaddingValues(
                horizontal = Dimension.pagePadding,
                vertical = Dimension.pagePadding.times(0.7f),
            ),
            backgroundColor = MaterialTheme.colors.surface,
            textColor = MaterialTheme.colors.onBackground,
            imeAction = ImeAction.Done,
            shape = MaterialTheme.shapes.large,
            leadingIcon = {
                Icon(
                    modifier = Modifier
                        .padding(end = Dimension.pagePadding.div(2))
                        .size(Dimension.mdIcon.times(0.7f)),
                    painter = painterResource(id = R.drawable.ic_lock),
                    contentDescription = null,
                    tint = MaterialTheme.colors.onBackground.copy(alpha = 0.4f),
                )
            },
            onFocusChange = { },
            onKeyboardActionClicked = { },
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
            text = stringResource(id = R.string.login),
            enabled = uiState !is UiState.Loading,
            textStyle = MaterialTheme.typography.button,
            onButtonClicked = {
                /** Handle the click event of the login button */
                loginViewModel.authenticateUser(
                    emailOrPhone = emailOrPhone ?: "",
                    password = password ?: "",
                    onAuthenticated = {
                        /** When user is authenticated, go home or back */
                        onUserAuthenticated()
                    },
                    onAuthenticationFailed = {
                        /** Do whatever you want when it failed */
                        onToastRequested("Veuillez remplir tous les champs!", Color.Red)
                    }
                )
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
            buttonColor = MaterialTheme.colors.onSurface,
            contentColor = Color.White,
            text = "S'inscrire",
            textStyle = MaterialTheme.typography.button,
            onButtonClicked = {
                navController.navigate("register")
            }
        )
    }
}
