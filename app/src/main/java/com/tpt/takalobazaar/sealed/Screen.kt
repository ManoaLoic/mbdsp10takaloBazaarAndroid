package com.tpt.takalobazaar.sealed

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.vectorResource
import com.tpt.takalobazaar.R

sealed class Screen(
    val route: String,
    @StringRes val title: Int? = null,
    @DrawableRes val icon: Int? = null,
) {
    object Splash : Screen(
        route = "splash",
    )
    object Login : Screen(
        route = "login",
        title = R.string.login,
    )

    object Register : Screen(
        route = "register",
        title = R.string.register,
    )
    object Home : Screen(
        route = "home",
        title = R.string.home,
        icon = R.drawable.ic_home_empty,
    )
    object Profile : Screen(
        route = "profile",
        title = R.string.profile,
        icon = R.drawable.ic_profile_empty,
    )

    object Notifications : Screen(
        route = "notifications",
        title = R.string.notifications,
        icon = R.drawable.ic_notifications,
    )

    object Search : Screen(
        route = "search",
        title = R.string.search,
        icon = R.drawable.ic_search,
    )

    object ExchangeHistory : Screen(
        route = "exchangeHistory",
        title = R.string.historique_exchange,
        icon = R.drawable.ic_history,
    )

    object UpdateAccount : Screen(
        route = "updateAccount",
        title = R.string.modifier_account,
        icon = R.drawable.ic_lock,
    )

    object ChangePassword : Screen(
        route = "changePassword",
        title = R.string.change_password,
        icon = R.drawable.ic_terms,
    )

    object AjoutObjet : Screen(
        route = "ajoutobjet",
        title = R.string.ajout_objet,
        icon = R.drawable.ic_add
    )

    object CurrentExchange : Screen(
        route = "currentExchange",
        title = R.string.ajout_objet,
        icon = R.drawable.ic_exchange
    )

    object FicheEchange : Screen(
        route = "ficheExchange",
        title = R.string.ajout_objet,
        icon = R.drawable.ic_exchange
    )

    object MyObjects : Screen(
        route = "myobjects",
        title = R.string.my_objects,
        icon = R.drawable.ic_my_objects
    )

    object ProposerEchange : Screen(
        route = "proposerEchange",
        title = R.string.proposer_echange,
        icon = R.drawable.ic_exchange
    )
}
