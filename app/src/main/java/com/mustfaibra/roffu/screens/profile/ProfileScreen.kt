package com.mustfaibra.roffu.screens.profile

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.KeyboardArrowRight
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import coil.compose.rememberAsyncImagePainter
import com.mustfaibra.roffu.R
import com.mustfaibra.roffu.components.DrawableButton
import com.mustfaibra.roffu.components.IconButton
import com.mustfaibra.roffu.models.LoginUser
import com.mustfaibra.roffu.sealed.Screen
import com.mustfaibra.roffu.ui.theme.Dimension

@Composable
fun ProfileScreen(
    user: LoginUser,
    onNavigationRequested: (route: String, removePreviousRoute: Boolean) -> Unit,
    profileViewModel: ProfileViewModel = hiltViewModel(),
) {
    val isLoggingOut by rememberUpdatedState(profileViewModel.isLoggingOut.value)
    val generalOptions = remember {
        listOf(Screen.Settings, Screen.OrderHistory)
    }
    val personalOptions = remember {
        listOf(Screen.PrivacyPolicies, Screen.TermsConditions)
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
        /** Header section */
        item {
            // ProfileHeaderSection(...)
        }
        /** Add virtual card section */
        /** Add virtual card section */
        item {
            Card(
                modifier = Modifier.clickable { },
                shape = MaterialTheme.shapes.medium,
                backgroundColor = MaterialTheme.colors.secondary,
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
                            text = "Add virtual card",
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
                        text = "Virtual cards allow you to purchase products on the store.",
                        style = MaterialTheme.typography.body2,
                    )
                }
            }
        }
        /** General options */
        /** General options */
        item {
            Text(
                text = "General",
                style = MaterialTheme.typography.body1,
            )
        }
        items(generalOptions) { option ->
            ProfileOptionItem(
                icon = option.icon,
                title = option.title,
                onOptionClicked = {
                    onNavigationRequested(option.route, false)
                },
            )
        }
        /** Personal options */
        /** Personal options */
        item {
            Text(
                text = "Personal",
                style = MaterialTheme.typography.body1,
            )
        }
        items(personalOptions) { option ->
            ProfileOptionItem(
                icon = option.icon,
                title = option.title,
                onOptionClicked = {},
            )
        }
        /** Echanges section */
        /** Echanges section */
        item {
            Text(
                text = "Echanges",
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
        /** Logout button */
        /** Logout button */
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = Dimension.pagePadding)
                    .background(MaterialTheme.colors.primary, shape = MaterialTheme.shapes.medium)
                    .clickable {
                        profileViewModel.logOut(
                            onLogoutSuccess = {
                                onNavigationRequested(Screen.Home.route, true)
                            },
                            onLogoutFailure = {
                                onNavigationRequested(Screen.Home.route, true)
                            }
                        )
                    },
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                if (isLoggingOut) {
                    CircularProgressIndicator(
                        color = Color.White,
                        modifier = Modifier
                            .padding(vertical = Dimension.sm, horizontal = Dimension.lg)
                            .size(24.dp)
                    )
                } else {
                    Icon(
                        imageVector = Icons.Rounded.KeyboardArrowRight,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.padding(end = 8.dp)
                    )
                    Text(
                        text = "Se déconnecter",
                        style = MaterialTheme.typography.button,
                        color = Color.White,
                        modifier = Modifier
                            .padding(vertical = Dimension.sm, horizontal = Dimension.lg)
                    )
                }
            }
        }
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
fun ProfileHeaderSection(image: Int?, name: String, email: String?, phone: String?) {
    Row(
        modifier = Modifier
            .fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(Dimension.pagePadding),
    ) {
        AsyncImage(
            modifier = Modifier
                .size(Dimension.xlIcon)
                .clip(CircleShape),
            model = image,
            contentDescription = null,
        )

        Column {
            Text(
                text = name,
                style = MaterialTheme.typography.h5,
            )
            Text(
                text = email ?: "",
                style = MaterialTheme.typography.body1,
                color = MaterialTheme.colors.onBackground.copy(alpha = 0.7f),
            )
            Text(
                text = phone ?: "",
                style = MaterialTheme.typography.caption
                    .copy(fontWeight = FontWeight.Medium),
            )
        }
    }
}
