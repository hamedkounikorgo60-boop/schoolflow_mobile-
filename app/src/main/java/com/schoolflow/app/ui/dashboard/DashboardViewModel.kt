package com.schoolflow.app.ui.dashboard  
  
import androidx.compose.runtime.getValue  
import androidx.compose.runtime.mutableStateOf  
import androidx.compose.runtime.setValue  
import androidx.lifecycle.ViewModel  
import androidx.lifecycle.viewModelScope  
import com.schoolflow.app.data.local.TokenManager  
import com.schoolflow.app.data.model.DashboardResponse  
import com.schoolflow.app.data.repository.ApiResult  
import com.schoolflow.app.data.repository.EleveRepository  
import kotlinx.coroutines.flow.first  
import kotlinx.coroutines.launch  
  
data class DashboardUiState(  
    val isLoading: Boolean = true,  
    val errorMessage: String? = null,  
    val data: DashboardResponse? = null,  
    val trimestre: String = "trimestre1",  
    val nbNonLues: Int = 0  
)  
  
class DashboardViewModel(  
    private val tokenManager: TokenManager  
) : ViewModel() {  
  
    private val repository = EleveRepository()  
  
    var uiState by mutableStateOf(DashboardUiState())  
        private set  
  
    init {  
        loadDashboard()  
    }  
  
    fun loadDashboard(trimestre: String = uiState.trimestre) {  
        uiState = uiState.copy(isLoading = true, errorMessage = null, trimestre = trimestre)  
  
        viewModelScope.launch {  
            val token = tokenManager.token.first()  
            val eleveIdStr = tokenManager.eleveId.first()  
  
            if (token == null || eleveIdStr == null) {  
                uiState = uiState.copy(  
                    isLoading = false,  
                    errorMessage = "Session invalide. Reconnectez-vous."  
                )  
                return@launch  
            }  
  
            val eleveId = eleveIdStr.toIntOrNull() ?: return@launch  
  
            val result = repository.getDashboard(token, eleveId, trimestre)  
            when (result) {  
                is ApiResult.Success -> {  
                    uiState = uiState.copy(isLoading = false, data = result.data)  
                }  
                is ApiResult.Error -> {  
                    uiState = uiState.copy(isLoading = false, errorMessage = result.message)  
                }  
            }  
  
            // Charge le compteur de notifications non lues (pour le badge de la cloche)  
            when (val notif = repository.getNotifications(token, eleveId)) {  
                is ApiResult.Success -> {  
                    uiState = uiState.copy(nbNonLues = notif.data.nb_non_lues)  
                }  
                is ApiResult.Error -> { /* on ignore : le badge reste inchangé */ }  
            }  
        }  
    }  
  
    suspend fun logout() {  
        tokenManager.clear()  
    }  
}  
