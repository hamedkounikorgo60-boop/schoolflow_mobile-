package com.schoolflow.app.ui.login  
  
import androidx.compose.runtime.getValue  
import androidx.compose.runtime.mutableStateOf  
import androidx.compose.runtime.setValue  
import androidx.lifecycle.ViewModel  
import androidx.lifecycle.viewModelScope  
import com.schoolflow.app.data.local.TokenManager  
import com.schoolflow.app.data.model.EleveChoixDto  
import com.schoolflow.app.data.repository.ApiResult  
import com.schoolflow.app.data.repository.AuthRepository  
import kotlinx.coroutines.launch  
  
data class LoginUiState(  
    val email: String = "",  
    val password: String = "",  
    val isLoading: Boolean = false,  
    val errorMessage: String? = null,  
    val isLoggedIn: Boolean = false,  
    val userRole: String = "",  
    val eleveId: Int? = null  
)  
  
class LoginViewModel(  
    private val tokenManager: TokenManager  
) : ViewModel() {  
  
    private val repository = AuthRepository()  
  
    var uiState by mutableStateOf(LoginUiState())  
        private set  
  
    fun onEmailChange(value: String) {  
        uiState = uiState.copy(email = value, errorMessage = null)  
    }  
  
    fun onPasswordChange(value: String) {  
        uiState = uiState.copy(password = value, errorMessage = null)  
    }  
  
    fun login() {  
        if (uiState.email.isBlank() || uiState.password.isBlank()) {  
            uiState = uiState.copy(errorMessage = "Veuillez remplir tous les champs.")  
            return  
        }  
  
        uiState = uiState.copy(isLoading = true, errorMessage = null)  
  
        viewModelScope.launch {  
            val result = repository.login(uiState.email, uiState.password)  
            when (result) {  
                is ApiResult.Success -> {  
                    val user = result.data.user  
  
                    tokenManager.saveToken(  
                        token = "Bearer ${result.data.token}",  
                        userName = user.name,  
                        role = user.role  
                    )  
  
                    // Parent : on va vers l'écran "Mes enfants" (ChildrenScreen),  
                    // qui charge la liste et gère le choix de l'élève.  
                    uiState = uiState.copy(  
                        isLoading = false,  
                        isLoggedIn = true,  
                        userRole = user.role,  
                        eleveId = user.eleve_id  
                    )  
                }  
                is ApiResult.Error -> {  
                    uiState = uiState.copy(  
                        isLoading = false,  
                        errorMessage = result.message  
                    )  
                }  
            }  
        }  
    }  
}  
