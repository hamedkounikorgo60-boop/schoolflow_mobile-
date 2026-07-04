package com.schoolflow.app.data.repository

import com.schoolflow.app.data.api.RetrofitClient
import com.schoolflow.app.data.model.ChangePasswordRequest
import com.schoolflow.app.data.model.LoginRequest
import com.schoolflow.app.data.model.LoginResponse
import com.schoolflow.app.data.model.MessageResponse
import java.io.IOException
import java.net.SocketTimeoutException

sealed class ApiResult<out T> {
    data class Success<T>(val data: T) : ApiResult<T>()
    data class Error(val message: String) : ApiResult<Nothing>()
}

class AuthRepository {

    suspend fun login(email: String, password: String): ApiResult<LoginResponse> {
        return try {
            val response = RetrofitClient.apiService.login(LoginRequest(email.trim(), password))
            if (response.isSuccessful && response.body() != null) {
                ApiResult.Success(response.body()!!)
            } else {
                ApiResult.Error(loginErrorMessage(response.code()))
            }
        } catch (e: Exception) {
            ApiResult.Error(networkErrorMessage(e))
        }
    }

    suspend fun changePassword(
        token: String,
        currentPassword: String,
        newPassword: String
    ): ApiResult<MessageResponse> {
        return try {
            val response = RetrofitClient.apiService.changePassword(
                token,
                ChangePasswordRequest(
                    current_password = currentPassword,
                    new_password = newPassword,
                    new_password_confirmation = newPassword
                )
            )
            if (response.isSuccessful && response.body() != null) {
                ApiResult.Success(response.body()!!)
            } else {
                ApiResult.Error(
                    when (response.code()) {
                        401 -> "Session expirée. Reconnectez-vous."
                        422 -> "Mot de passe actuel incorrect ou nouveau mot de passe invalide."
                        else -> "Erreur serveur (${response.code()})."
                    }
                )
            }
        } catch (e: Exception) {
            ApiResult.Error(networkErrorMessage(e))
        }
    }

    private fun loginErrorMessage(code: Int): String {
        return when (code) {
            401 -> "Email ou mot de passe incorrect."
            403 -> "Accès refusé pour ce compte."
            422 -> "Vérifiez le format de l'email et du mot de passe."
            500 -> "Erreur interne du serveur Laravel. Consultez les logs."
            else -> "Erreur serveur ($code)."
        }
    }
}

fun networkErrorMessage(error: Throwable): String {
    return when (error) {
        is SocketTimeoutException -> "Le serveur ne répond pas. Vérifiez l'adresse API et le réseau."
        is IOException -> "Impossible de joindre le serveur. Vérifiez l'IP, le port et la connexion."
        else -> "Erreur inattendue : ${error.message ?: "cause inconnue"}."
    }
}
