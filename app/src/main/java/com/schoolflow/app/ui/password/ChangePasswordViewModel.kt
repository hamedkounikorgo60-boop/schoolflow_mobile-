package com.schoolflow.app.ui.password

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.schoolflow.app.data.local.TokenManager
import com.schoolflow.app.data.repository.ApiResult
import com.schoolflow.app.data.repository.AuthRepository
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

data class ChangePasswordUiState(
    val currentPassword: String = "",
    val newPassword: String = "",
    val confirmPassword: String = "",
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val successMessage: String? = null
)

class ChangePasswordViewModel(
    private val tokenManager: TokenManager
) : ViewModel() {

    private val repository = AuthRepository()

    var uiState by mutableStateOf(ChangePasswordUiState())
        private set

    fun onCurrentPasswordChange(value: String) {
        uiState = uiState.copy(currentPassword = value, errorMessage = null)
    }

    fun onNewPasswordChange(value: String) {
        uiState = uiState.copy(newPassword = value, errorMessage = null)
    }

    fun onConfirmPasswordChange(value: String) {
        uiState = uiState.copy(confirmPassword = value, errorMessage = null)
    }

    fun submit() {
        if (uiState.currentPassword.isBlank() || uiState.newPassword.isBlank()) {
            uiState = uiState.copy(errorMessage = "Veuillez remplir tous les champs.")
            return
        }
        if (uiState.newPassword.length < 6) {
            uiState = uiState.copy(errorMessage = "Le nouveau mot de passe doit contenir au moins 6 caractères.")
            return
        }
        if (uiState.newPassword != uiState.confirmPassword) {
            uiState = uiState.copy(errorMessage = "Les mots de passe ne correspondent pas.")
            return
        }

        uiState = uiState.copy(isLoading = true, errorMessage = null, successMessage = null)

        viewModelScope.launch {
            val token = tokenManager.token.first()
            if (token == null) {
                uiState = uiState.copy(isLoading = false, errorMessage = "Session expirée.")
                return@launch
            }

            val result = repository.changePassword(token, uiState.currentPassword, uiState.newPassword)
            when (result) {
                is ApiResult.Success -> {
                    uiState = ChangePasswordUiState(successMessage = "Mot de passe modifié avec succès.")
                }
                is ApiResult.Error -> {
                    uiState = uiState.copy(isLoading = false, errorMessage = result.message)
                }
            }
        }
    }
}
