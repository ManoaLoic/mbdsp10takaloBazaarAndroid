package com.tpt.takalobazaar.services

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import com.google.gson.Gson
import com.tpt.takalobazaar.models.LoginUser
import com.tpt.takalobazaar.utils.UserPref
import com.tpt.takalobazaar.utils.dataStore
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.runBlocking
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SessionService @Inject constructor(
    public val context: Context
) {
    private val gson = Gson()
    private var cachedUser: LoginUser? = null

    suspend fun getUser(): LoginUser? {
        if (cachedUser == null) {
            val preferences = context.dataStore.data.first()
            val userJson = preferences[UserPref.LOGGED_USER_INFO]
            cachedUser = userJson?.let { gson.fromJson(it, LoginUser::class.java) }
        }
        return cachedUser
    }

    suspend fun saveUser(user: LoginUser) {
        val userJson = gson.toJson(user)
        context.dataStore.edit { preferences ->
            preferences[UserPref.LOGGED_USER_INFO] = userJson
        }
        cachedUser = user
    }

    suspend fun clearUser() {
        context.dataStore.edit { preferences ->
            preferences.remove(UserPref.LOGGED_USER_INFO)
        }
        cachedUser = null
    }

    fun observeUser() = context.dataStore.data.map { preferences ->
        val userJson = preferences[UserPref.LOGGED_USER_INFO]
        userJson?.let { gson.fromJson(it, LoginUser::class.java) }
    }

    fun getCachedToken(): String? {
        return cachedUser?.token
    }

    fun fetchUserSynchronously() {
        runBlocking {
            getUser()
        }
    }
}
