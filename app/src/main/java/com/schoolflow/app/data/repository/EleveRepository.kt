package com.schoolflow.app.data.repository  
  
import com.schoolflow.app.data.api.RetrofitClient  
import com.schoolflow.app.data.model.AbsencesResponse  
import com.schoolflow.app.data.model.CreatePaiementRequest  
import com.schoolflow.app.data.model.DashboardResponse  
import com.schoolflow.app.data.model.EnfantsResponse  
import com.schoolflow.app.data.model.MessageResponse  
import com.schoolflow.app.data.model.NotesResponse  
import com.schoolflow.app.data.model.NotificationsResponse  
import com.schoolflow.app.data.model.PaiementDto  
import com.schoolflow.app.data.model.PaiementsResponse  
  
class EleveRepository {  
  
    suspend fun getEnfants(token: String): ApiResult<EnfantsResponse> {  
        return safeApiCall("chargement des enfants") {  
            RetrofitClient.apiService.getEnfants(token)  
        }  
    }  
  
    suspend fun getDashboard(token: String, eleveId: Int, trimestre: String): ApiResult<DashboardResponse> {  
        return safeApiCall("chargement du tableau de bord") {  
            RetrofitClient.apiService.getDashboard(token, eleveId, trimestre)  
        }  
    }  
  
    suspend fun getNotes(token: String, eleveId: Int, trimestre: String): ApiResult<NotesResponse> {  
        return safeApiCall("chargement des notes") {  
            RetrofitClient.apiService.getNotes(token, eleveId, trimestre)  
        }  
    }  
  
    suspend fun getPaiements(token: String, eleveId: Int): ApiResult<PaiementsResponse> {  
        return safeApiCall("chargement des paiements") {  
            RetrofitClient.apiService.getPaiements(token, eleveId)  
        }  
    }  
  
    suspend fun createPaiement(  
        token: String,  
        eleveId: Int,  
        montant: Double,  
        type: String,  
        modePaiement: String,  
        numero: String  
    ): ApiResult<PaiementDto> {  
        return safeApiCall("déclaration du paiement") {  
            RetrofitClient.apiService.createPaiement(  
                token,  
                eleveId,  
                CreatePaiementRequest(montant, type, modePaiement, numero)  
            )  
        }  
    }  
  
    suspend fun getAbsences(token: String, eleveId: Int): ApiResult<AbsencesResponse> {  
        return safeApiCall("chargement des absences") {  
            RetrofitClient.apiService.getAbsences(token, eleveId)  
        }  
    }  
  
    suspend fun getNotifications(token: String, eleveId: Int): ApiResult<NotificationsResponse> {  
        return safeApiCall("chargement des notifications") {  
            RetrofitClient.apiService.getNotifications(token, eleveId)  
        }  
    }  
  
    suspend fun markNotificationRead(token: String, eleveId: Int, notifId: Int): ApiResult<MessageResponse> {  
        return safeApiCall("mise à jour de la notification") {  
            RetrofitClient.apiService.markNotificationRead(token, eleveId, notifId)  
        }  
    }  
  
    private suspend fun <T> safeApiCall(  
        operation: String,  
        call: suspend () -> retrofit2.Response<T>  
    ): ApiResult<T> {  
        return try {  
            val response = call()  
            val body = response.body()  
            if (response.isSuccessful && body != null) {  
                ApiResult.Success(body)  
            } else {  
                ApiResult.Error(apiErrorMessage(response.code(), operation))  
            }  
        } catch (e: Exception) {  
            ApiResult.Error(networkErrorMessage(e))  
        }  
    }  
  
    private fun apiErrorMessage(code: Int, operation: String): String {  
        return when (code) {  
            401 -> "Session expirée. Reconnectez-vous."  
            403 -> "Accès refusé à cette ressource."  
            404 -> "Ressource introuvable pendant le $operation."  
            422 -> "Données invalides pour le $operation."  
            500 -> "Erreur interne du serveur Laravel pendant le $operation."  
            else -> "Erreur pendant le $operation ($code)."  
        }  
    }  
}  
