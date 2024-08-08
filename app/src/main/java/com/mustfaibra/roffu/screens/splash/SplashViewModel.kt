package com.mustfaibra.roffu.screens.splash

import android.annotation.SuppressLint
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mustfaibra.roffu.models.LoginUser
import com.mustfaibra.roffu.services.SessionService
import com.mustfaibra.roffu.utils.UserPref
import com.mustfaibra.roffu.utils.dataStore
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
@SuppressLint("StaticFieldLeak")
class SplashViewModel @Inject constructor(
    private val sessionService: SessionService
) : ViewModel() {
    val isAppLaunchedBefore = sessionService.context.dataStore.data.map {
        it[UserPref.APP_LAUNCHED] ?: false
    }

    fun checkLoggedUser(onCheckFinish: (LoginUser?) -> Unit) {
        viewModelScope.launch {
            val user = sessionService.getUser()
            user?.let {
                Timber.d("Logged user exists!")
            } ?: Timber.d("No logged user found!")
            onCheckFinish(user)
        }
    }
}
