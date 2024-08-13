package com.tpt.takalobazaar.utils

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import com.tpt.takalobazaar.models.CustomUser
import com.tpt.takalobazaar.models.LoginUser

object UserPref {
    val LOGGED_USER_ID = intPreferencesKey("logged_user_id")
    val LOGGED_USER_INFO = stringPreferencesKey("logged_user_info")
    val APP_LAUNCHED = booleanPreferencesKey("app_launched_info")
}