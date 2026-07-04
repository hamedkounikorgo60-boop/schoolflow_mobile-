package com.schoolflow.app.ui.notifications  
  
import androidx.compose.runtime.getValue  
import androidx.compose.runtime.mutableStateOf  
import androidx.compose.runtime.setValue  
import androidx.lifecycle.ViewModel  
import androidx.lifecycle.ViewModelProvider  
import androidx.lifecycle.viewModelScope  
import com.schoolflow.app.data.local.TokenManager  
import com.schoolflow.app.data.model.NotificationDto  
import com.schoolflow.app.data.repository.ApiResult  
import com.schoolflow.app.data.repository.EleveRepository  
import kotlinx.coroutines.flow.first  
import kotlinx.coroutines.launch  
  
data class NotificationsUiState(  
    val isLoading: Boolean = false,  
    val notifications: List<NotificationDto> = emptyList(),  
    val errorMessage: String? = null  
)  
  
class NotificationsViewModel(private val tokenManager: TokenManager) : ViewModel() {  
    private val repository = EleveRepository()  
    var uiState by mutableStateOf(NotificationsUiState())  
        private set  
  
    fun load() {  
        viewModelScope.launch {  
            uiState = uiState.copy(isLoading = true, errorMessage = null)  
            val token = tokenManager.token.first()  
            val eleveId = tokenManager.eleveId.first()?.toIntOrNull()  
            if (token == null || eleveId == null) {  
                uiState = uiState.copy(isLoading = false, errorMessage = "Session invalide.")  
                return@launch  
            }  
            when (val result = repository.getNotifications(token, eleveId)) {  
                is ApiResult.Success -> uiState = uiState.copy(  
                    isLoading = false,  
                    notifications = result.data.notifications  
                )  
                is ApiResult.Error -> uiState = uiState.copy(  
                    isLoading = false,  
                    errorMessage = result.message  
                )  
            }  
        }  
    }  
  
    fun markRead(notifId: Int) {  
        viewModelScope.launch {  
            val token = tokenManager.token.first() ?: return@launch  
            val eleveId = tokenManager.eleveId.first()?.toIntOrNull() ?: return@launch  
            repository.markNotificationRead(token, eleveId, notifId)  
            load()  
        }  
    }  
}  
  
class NotificationsViewModelFactory(  
    private val tokenManager: TokenManager  
) : ViewModelProvider.Factory {  
    override fun <T : ViewModel> create(modelClass: Class<T>): T {  
        @Suppress("UNCHECKED_CAST")  
        return NotificationsViewModel(tokenManager) as T  
    }  
}  
