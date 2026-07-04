package com.schoolflow.app.ui.paiements

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.schoolflow.app.data.local.TokenManager

class PaiementsViewModelFactory(
    private val tokenManager: TokenManager
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(PaiementsViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return PaiementsViewModel(tokenManager) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
