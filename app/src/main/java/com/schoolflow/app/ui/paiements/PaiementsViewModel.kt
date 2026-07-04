package com.schoolflow.app.ui.paiements  
  
import androidx.compose.runtime.getValue  
import androidx.compose.runtime.mutableStateOf  
import androidx.compose.runtime.setValue  
import androidx.lifecycle.ViewModel  
import androidx.lifecycle.viewModelScope  
import com.schoolflow.app.data.local.TokenManager  
import com.schoolflow.app.data.model.PaiementsResponse  
import com.schoolflow.app.data.repository.ApiResult  
import com.schoolflow.app.data.repository.EleveRepository  
import kotlinx.coroutines.flow.first  
import kotlinx.coroutines.launch  
  
data class PaiementsUiState(  
    val isLoading: Boolean = true,  
    val errorMessage: String? = null,  
    val data: PaiementsResponse? = null,  
    val isSubmitting: Boolean = false,  
    val submitError: String? = null,  
    val submitSuccess: Boolean = false  
)  
  
class PaiementsViewModel(  
    private val tokenManager: TokenManager  
) : ViewModel() {  
  
    private val repository = EleveRepository()  
  
    var uiState by mutableStateOf(PaiementsUiState())  
        private set  
  
    init {  
        loadPaiements()  
    }  
  
    fun loadPaiements() {  
        uiState = uiState.copy(isLoading = true, errorMessage = null)  
  
        viewModelScope.launch {  
            val token = tokenManager.token.first()  
            val eleveIdStr = tokenManager.eleveId.first()  
  
            if (token == null || eleveIdStr == null) {  
                uiState = uiState.copy(isLoading = false, errorMessage = "Session invalide.")  
                return@launch  
            }  
  
            val eleveId = eleveIdStr.toIntOrNull() ?: return@launch  
  
            val result = repository.getPaiements(token, eleveId)  
            when (result) {  
                is ApiResult.Success -> {  
                    uiState = uiState.copy(isLoading = false, data = result.data)  
                }  
                is ApiResult.Error -> {  
                    uiState = uiState.copy(isLoading = false, errorMessage = result.message)  
                }  
            }  
        }  
    }  
  
    fun createPaiement(montant: Double, type: String, modePaiement: String, numero: String) {  
        uiState = uiState.copy(isSubmitting = true, submitError = null, submitSuccess = false)  
  
        viewModelScope.launch {  
            val token = tokenManager.token.first()  
            val eleveIdStr = tokenManager.eleveId.first()  
  
            if (token == null || eleveIdStr == null) {  
                uiState = uiState.copy(isSubmitting = false, submitError = "Session invalide.")  
                return@launch  
            }  
  
            val eleveId = eleveIdStr.toIntOrNull() ?: return@launch  
  
            val result = repository.createPaiement(token, eleveId, montant, type, modePaiement, numero)  
            when (result) {  
                is ApiResult.Success -> {  
                    uiState = uiState.copy(isSubmitting = false, submitSuccess = true)  
                    loadPaiements()  
                }  
                is ApiResult.Error -> {  
                    uiState = uiState.copy(isSubmitting = false, submitError = result.message)  
                }  
            }  
        }  
    }  
  
    fun clearSubmitState() {  
        uiState = uiState.copy(submitError = null, submitSuccess = false)  
    }  
}  
