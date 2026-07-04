package com.schoolflow.app.ui.absences

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.schoolflow.app.data.local.TokenManager
import com.schoolflow.app.data.model.AbsencesResponse
import com.schoolflow.app.data.repository.ApiResult
import com.schoolflow.app.data.repository.EleveRepository
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

data class AbsencesUiState(
    val isLoading: Boolean = true,
    val errorMessage: String? = null,
    val data: AbsencesResponse? = null
)

class AbsencesViewModel(
    private val tokenManager: TokenManager
) : ViewModel() {

    private val repository = EleveRepository()

    var uiState by mutableStateOf(AbsencesUiState())
        private set

    init {
        loadAbsences()
    }

    fun loadAbsences() {
        uiState = uiState.copy(isLoading = true, errorMessage = null)

        viewModelScope.launch {
            val token = tokenManager.token.first()
            val eleveIdStr = tokenManager.eleveId.first()

            if (token == null || eleveIdStr == null) {
                uiState = uiState.copy(isLoading = false, errorMessage = "Session invalide.")
                return@launch
            }

            val eleveId = eleveIdStr.toIntOrNull() ?: return@launch

            val result = repository.getAbsences(token, eleveId)
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
}
