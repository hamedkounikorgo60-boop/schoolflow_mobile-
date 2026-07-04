package com.schoolflow.app.ui.absences

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.schoolflow.app.data.local.TokenManager

class AbsencesViewModelFactory(
    private val tokenManager: TokenManager
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(AbsencesViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return AbsencesViewModel(tokenManager) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
