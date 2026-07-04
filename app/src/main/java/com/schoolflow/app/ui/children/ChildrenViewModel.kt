package com.schoolflow.app.ui.children  
  
import androidx.compose.runtime.getValue  
import androidx.compose.runtime.mutableStateOf  
import androidx.compose.runtime.setValue  
import androidx.lifecycle.ViewModel  
import androidx.lifecycle.viewModelScope  
import com.schoolflow.app.data.local.TokenManager  
import com.schoolflow.app.data.model.EnfantsResponse  
import com.schoolflow.app.data.repository.ApiResult  
import com.schoolflow.app.data.repository.EleveRepository  
import kotlinx.coroutines.flow.first  
import kotlinx.coroutines.launch  
  
data class ChildrenUiState(  
    val isLoading: Boolean = true,  
    val errorMessage: String? = null,  
    val data: EnfantsResponse? = null  
)  
  
class ChildrenViewModel(  
    private val tokenManager: TokenManager  
) : ViewModel() {  
  
    private val repository = EleveRepository()  
  
    var uiState by mutableStateOf(ChildrenUiState())  
        private set  
  
    init {  
        loadEnfants()  
    }  
  
    fun loadEnfants() {  
        uiState = uiState.copy(isLoading = true, errorMessage = null)  
        viewModelScope.launch {  
            val token = tokenManager.token.first()  
            if (token == null) {  
                uiState = uiState.copy(isLoading = false, errorMessage = "Session invalide.")  
                return@launch  
            }  
            val result = repository.getEnfants(token)  
            when (result) {  
                is ApiResult.Success -> uiState = uiState.copy(isLoading = false, data = result.data)  
                is ApiResult.Error -> uiState = uiState.copy(isLoading = false, errorMessage = result.message)  
            }  
        }  
    }  
  
    fun selectEnfant(eleveId: Int, onSelected: () -> Unit) {  
        viewModelScope.launch {  
            tokenManager.saveEleveId(eleveId)  
            onSelected()  
        }  
    }  
}  
