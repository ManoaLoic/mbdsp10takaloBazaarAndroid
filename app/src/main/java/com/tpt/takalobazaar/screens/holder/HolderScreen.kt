package com.tpt.takalobazaar.screens.holder

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.TweenSpec
import androidx.compose.animation.core.animateDp
import androidx.compose.animation.core.updateTransition
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.material.ScaffoldState
import androidx.compose.material.SnackbarDuration
import androidx.compose.material.rememberScaffoldState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.IntOffset
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.navArgument
import com.tpt.takalobazaar.screens.userprofile.UserProfileScreen
import com.tpt.takalobazaar.api.RetrofitInstance
import com.tpt.takalobazaar.components.AppBottomNav
import com.tpt.takalobazaar.components.CustomSnackBar
import com.tpt.takalobazaar.models.LoginUser
import com.tpt.takalobazaar.providers.LocalNavHost
import com.tpt.takalobazaar.screens.ChangePassword.ChangePasswordScreen
import com.tpt.takalobazaar.screens.ajoutobjet.AjoutObjetScreen
import com.tpt.takalobazaar.screens.CurrentExchange.CurrentExchangeScreen
import com.tpt.takalobazaar.screens.ExchangeHistory.ExchangeHistoryScreen
import com.tpt.takalobazaar.screens.FicheEchangeScreen.FicheExchangeScreen
import com.tpt.takalobazaar.screens.ProposerEchange.ProposerEchangeScreen
import com.tpt.takalobazaar.screens.editobject.EditObjectScreen
import com.tpt.takalobazaar.screens.ficheobjet.FicheObjetScreen
import com.tpt.takalobazaar.screens.home.HomeScreen
import com.tpt.takalobazaar.screens.login.LoginScreen
import com.tpt.takalobazaar.screens.notifications.NotificationScreen
import com.tpt.takalobazaar.screens.objectsearch.ObjectSearchScreen
import com.tpt.takalobazaar.screens.profile.ProfileScreen
import com.tpt.takalobazaar.screens.search.SearchScreen
import com.tpt.takalobazaar.screens.splash.SplashScreen
import com.tpt.takalobazaar.sealed.Screen
import com.tpt.takalobazaar.utils.getDp
import com.skydoves.whatif.whatIfNotNull
import com.tpt.takalobazaar.screens.modifierprofil.ModifierProfilScreen
import kotlinx.coroutines.launch
import com.tpt.takalobazaar.screens.myobjects.MyObjectsScreen
import com.tpt.takalobazaar.screens.register.RegisterScreen

@Composable
fun HolderScreen(
    onStatusBarColorChange: (color: Color) -> Unit,
    holderViewModel: HolderViewModel = hiltViewModel(),
    navController: NavHostController
) {
    val destinations = remember { listOf(Screen.Home, Screen.Profile) }
    val controller = LocalNavHost.current
    val currentRouteAsState = getActiveRoute(navController = controller)
    val scaffoldState = rememberScaffoldState()
    val scope = rememberCoroutineScope()
    val (snackBarColor, setSnackBarColor) = remember { mutableStateOf(Color.White) }
    val snackBarTransition = updateTransition(targetState = scaffoldState.snackbarHostState, label = "SnackBarTransition")
    val snackBarOffsetAnim by snackBarTransition.animateDp(label = "snackBarOffsetAnim", transitionSpec = {
        TweenSpec(durationMillis = 300, easing = LinearEasing)
    }) {
        if (it.currentSnackbarData != null) 0.getDp() else 100.getDp()
    }

    val user by holderViewModel.user.collectAsState()

    LaunchedEffect(Unit) {
        RetrofitInstance.setOnUnauthorizedCallback {
            scope.launch {
                navController.navigate(Screen.Login.route)
            }
        }
    }

    Box {
        val (cartOffset, setCartOffset) = remember { mutableStateOf(IntOffset(0, 0)) }
        ScaffoldSection(
            controller = controller,
            scaffoldState = scaffoldState,
            user = user,
            onStatusBarColorChange = onStatusBarColorChange,
            bottomNavigationContent = {
                if (currentRouteAsState in destinations.map { it.route } || currentRouteAsState == Screen.AjoutObjet.route) {
                    AppBottomNav(
                        activeRoute = currentRouteAsState,
                        backgroundColor = MaterialTheme.colors.surface,
                        bottomNavDestinations = destinations,
                        onCartOffsetMeasured = { offset -> setCartOffset(offset) },
                        onActiveRouteChange = {
                            if (it != currentRouteAsState) {
                                controller.navigate(it) {
                                    popUpTo(Screen.Home.route) { saveState = true }
                                    launchSingleTop = true
                                    restoreState = true
                                }
                            }
                        }
                    )
                }
            },
            onSplashFinished = { nextDestination ->
                controller.navigate(nextDestination.route) {
                    popUpTo(Screen.Splash.route) { inclusive = true }
                }
            },
            onBackRequested = {
                if (!controller.popBackStack()) {
                    controller.navigate(Screen.Home.route)
                }
            },
            onNavigationRequested = { route, removePreviousRoute ->
                if (removePreviousRoute) {
                    controller.popBackStack()
                }
                controller.navigate(route)
            },
            onUserNotAuthorized = {
                controller.navigate(Screen.Login.route)
            },
            onToastRequested = { message, color ->
                scope.launch {
                    scaffoldState.snackbarHostState.currentSnackbarData?.dismiss()
                    setSnackBarColor(color)
                    scaffoldState.snackbarHostState.showSnackbar(message = message, duration = SnackbarDuration.Short)
                }
            }
        )

        CustomSnackBar(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .offset(y = snackBarOffsetAnim),
            snackHost = scaffoldState.snackbarHostState,
            backgroundColorProvider = { snackBarColor }
        )
    }
}

@Composable
fun ScaffoldSection(
    controller: NavHostController,
    scaffoldState: ScaffoldState,
    user: LoginUser?,
    onStatusBarColorChange: (color: Color) -> Unit,
    onSplashFinished: (nextDestination: Screen) -> Unit,
    onNavigationRequested: (route: String, removePreviousRoute: Boolean) -> Unit,
    onBackRequested: () -> Unit,
    onUserNotAuthorized: () -> Unit,
    onToastRequested: (message: String, color: Color) -> Unit,
    bottomNavigationContent: @Composable () -> Unit,
) {
    Scaffold(
        scaffoldState = scaffoldState,
        snackbarHost = { scaffoldState.snackbarHostState },
    ) { paddingValues ->
        Column(Modifier.padding(paddingValues)) {
            NavHost(
                modifier = Modifier.weight(1f),
                navController = controller,
                startDestination = Screen.Splash.route
            ) {
                composable(Screen.Splash.route) {
                    onStatusBarColorChange(MaterialTheme.colors.background)
                    SplashScreen(onSplashFinished = onSplashFinished)
                }
                composable(Screen.Login.route) {
                    onStatusBarColorChange(MaterialTheme.colors.background)
                    LoginScreen(
                        onUserAuthenticated = {
                            controller.navigate(Screen.Home.route) {
                                popUpTo(Screen.Login.route) { inclusive = true }
                            }
                        },
                        onToastRequested = onToastRequested,
                        navController = controller
                    )
                }
                composable(Screen.Register.route) {
                    onStatusBarColorChange(MaterialTheme.colors.background)
                    RegisterScreen(
                        navController = controller,
                        onToastRequested = onToastRequested,
                    )
                }
                composable(Screen.Home.route) {
                    onStatusBarColorChange(MaterialTheme.colors.background)
                    HomeScreen(
                        navController = controller
                    )
                }
                composable(Screen.Notifications.route) {
                    onStatusBarColorChange(MaterialTheme.colors.background)
                    NotificationScreen()
                }
                composable(Screen.Search.route) {
                    onStatusBarColorChange(MaterialTheme.colors.background)
                    SearchScreen()
                }
                composable(Screen.Profile.route) {
                    user.whatIfNotNull(
                        whatIf = {
                            onStatusBarColorChange(MaterialTheme.colors.background)
                            ProfileScreen(
                                onNavigationRequested = onNavigationRequested,
                            )
                        },
                        whatIfNot = {
                            LoginScreen(
                                onUserAuthenticated = {
                                    controller.navigate(Screen.Home.route) {
                                        popUpTo(Screen.Login.route) { inclusive = true }
                                    }
                                },
                                onToastRequested = onToastRequested,
                                navController = controller
                            )
                        },
                    )
                }
                composable(Screen.AjoutObjet.route) {
                    onStatusBarColorChange(MaterialTheme.colors.background)
                    AjoutObjetScreen(navController = controller)
                }
                composable(Screen.CurrentExchange.route) {
                    onStatusBarColorChange(MaterialTheme.colors.background)
                    CurrentExchangeScreen(navController = controller)
                }
                composable(
                    "ficheobjet/{objectId}",
                    arguments = listOf(navArgument("objectId") { type = NavType.IntType })
                ) { backStackEntry ->
                    val objectId = backStackEntry.arguments?.getInt("objectId") ?: 0
                    FicheObjetScreen(navController = controller, objectId)
                }
                composable(
                    "editobject/{objectId}",
                    arguments = listOf(navArgument("objectId") { type = NavType.IntType })
                ) { backStackEntry ->
                    val objectId = backStackEntry.arguments?.getInt("objectId") ?: 0
                    EditObjectScreen(navController = controller, objectId)
                }
                composable(
                    "changePassword/{userId}",
                    arguments = listOf(navArgument("userId") { type = NavType.IntType })
                ) { backStackEntry ->
                    val userId = backStackEntry.arguments?.getInt("userId") ?: 0
                    ChangePasswordScreen(navController = controller, userId = userId)
                }
                composable(
                    "ficheExchange/{exchangeId}",
                    arguments = listOf(navArgument("exchangeId") { type = NavType.IntType })
                ) { backStackEntry ->
                    val exchangeId = backStackEntry.arguments?.getInt("exchangeId") ?: 0
                    FicheExchangeScreen(navController = controller, exchangeId)
                }
                composable(
                    route = "objectsearch?query={query}",
                    arguments = listOf(navArgument("query") {
                        type = NavType.StringType
                        nullable = true
                    })
                ) { backStackEntry ->
                    val query = backStackEntry.arguments?.getString("query") ?: ""
                    ObjectSearchScreen(navController = controller, searchQuery = query)
                }
                composable(
                    route = "${Screen.ProposerEchange}/{objectId}",
                    arguments = listOf(
                        navArgument("objectId") { type = NavType.IntType }
                    )
                ) { backStackEntry ->
                    val objectId = backStackEntry.arguments?.getInt("objectId") ?: 0
                    ProposerEchangeScreen(navController = controller, objectId = objectId)
                }
                composable(Screen.MyObjects.route) {
                    MyObjectsScreen(navController = controller)
                }
                composable(Screen.ExchangeHistory.route) {
                    ExchangeHistoryScreen(navController = controller)
                }
                composable("userprofile/{userId}") { backStackEntry ->
                    val userId = backStackEntry.arguments?.getString("userId")?.toIntOrNull()
                    userId?.let {
                        UserProfileScreen(navController = controller, userId = it)
                    }
                }
                composable(
                    route = "modifProfil/{userId}",
                    arguments = listOf(navArgument("userId") { type = NavType.IntType })
                ) { backStackEntry ->
                    val userId = backStackEntry.arguments?.getInt("userId") ?: 0
                    ModifierProfilScreen(navController = controller, userId = userId)
                }

            }
            bottomNavigationContent()
        }
    }
}

@Composable
fun getActiveRoute(navController: NavHostController): String {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    return navBackStackEntry?.destination?.route ?: "splash"
}
