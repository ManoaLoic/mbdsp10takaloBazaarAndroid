package com.tpt.takalobazaar.screens.ficheobjet

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tpt.takalobazaar.api.ObjectService
import com.tpt.takalobazaar.api.ReportService
import com.tpt.takalobazaar.api.RetrofitManager
import com.tpt.takalobazaar.models.LoginUser
import com.tpt.takalobazaar.models.Object
import com.tpt.takalobazaar.models.ReportRequest
import com.tpt.takalobazaar.services.SessionService
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import javax.inject.Inject

@HiltViewModel
class FicheObjetViewModel @Inject constructor(
    private val objectService: ObjectService,
    retrofitManager: RetrofitManager,
    public val sessionService: SessionService,
) : ViewModel() {
    private val reportService: ReportService = retrofitManager.createService(ReportService::class.java)
    private val _objectState = MutableStateFlow<ObjectState>(ObjectState.Loading)
    val objectState: StateFlow<ObjectState> get() = _objectState

    private val _reportTypes = MutableStateFlow<List<String>>(emptyList())
    val reportTypes: StateFlow<List<String>> get() = _reportTypes

    private val _isReporting = MutableStateFlow(false)
    val isReporting: StateFlow<Boolean> get() = _isReporting

    fun fetchObjectById(objectId: Int) {
        viewModelScope.launch {
            try {
                val response = objectService.getObjectById(objectId)
                if (response.isSuccessful) {
                    _objectState.value = ObjectState.Success(response.body()!!)
                } else {
                    _objectState.value =
                        ObjectState.Error("Erreur lors de la récupération de l'objet.")
                }
            } catch (e: Exception) {
                _objectState.value = ObjectState.Error("Erreur: ${e.message}")
            }
        }
    }


    fun getCurrentUser(): Flow<LoginUser?> = flow {
        val user = runBlocking { sessionService.getUser() }
        emit(user)
    }

    fun repostObject(objectId: Int, onSuccess: () -> Unit, onError: (String) -> Unit) {
        viewModelScope.launch {
            try {
                val response = objectService.repostObject(objectId)
                if (response.isSuccessful) {
                    onSuccess()
                } else {
                    val errorBody = response.errorBody()?.string()
                    onError("Erreur lors de la republication de l'objet : ${response.code()} ${response.message()} ${errorBody ?: ""}")
                }
            } catch (e: Exception) {
                onError("Erreur: ${e.message}")
            }
        }
    }


    fun removeObject(objectId: Int, onSuccess: () -> Unit, onError: (String) -> Unit) {
        viewModelScope.launch {
            try {
                val response = objectService.removeObject(objectId)
                if (response.isSuccessful) {
                    onSuccess()
                } else {
                    onError("Erreur lors du retrait de l'objet.")
                }
            } catch (e: Exception) {
                onError("Erreur: ${e.message}")
            }
        }
    }

    fun fetchReportTypes() {
        viewModelScope.launch {
            try {
                val response = reportService.getTypeReport()
                if (response.isSuccessful) {
                    _reportTypes.value = response.body()?.data?.typeReports?.map { it.name } ?: emptyList()
                } else {
                    _reportTypes.value = emptyList()
                }
            } catch (e: Exception) {
                _reportTypes.value = emptyList()
            }
        }
    }

    fun reportObject(objectId: Int, reportType: String, onSuccess: () -> Unit, onError: (String) -> Unit) {
        viewModelScope.launch {
            _isReporting.value = true
            try {
                val reportRequest = ReportRequest(objectId, reportType)
                val response = reportService.reportObject(reportRequest)
                if (response.isSuccessful) {
                    onSuccess()
                } else {
                    onError("Erreur lors du signalement.")
                }
            } catch (e: Exception) {
                onError("Erreur: ${e.message}")
            } finally {
                _isReporting.value = false
            }
        }
    }

    sealed class ObjectState {
        object Loading : ObjectState()
        data class Success(val obj: Object) : ObjectState()
        data class Error(val message: String) : ObjectState()
    }
}

