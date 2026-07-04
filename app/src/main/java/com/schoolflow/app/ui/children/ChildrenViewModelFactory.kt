package com.schoolflow.app.ui.children  
  
import androidx.lifecycle.ViewModel  
import androidx.lifecycle.ViewModelProvider  
import com.schoolflow.app.data.local.TokenManager  
  
class ChildrenViewModelFactory(  
    private val tokenManager: TokenManager  
) : ViewModelProvider.Factory {  
    override fun <T : ViewModel> create(modelClass: Class<T>): T {  
        if (modelClass.isAssignableFrom(ChildrenViewModel::class.java)) {  
            @Suppress("UNCHECKED_CAST")  
            return ChildrenViewModel(tokenManager) as T  
        }  
        throw IllegalArgumentException("Unknown ViewModel class")  
    }  
}  
