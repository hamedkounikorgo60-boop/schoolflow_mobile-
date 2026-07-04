package com.schoolflow.app.data.api  
  
import com.schoolflow.app.data.model.*  
import retrofit2.Response  
import retrofit2.http.*  
  
interface ApiService {  
  
    @POST("login")  
    suspend fun login(@Body request: LoginRequest): Response<LoginResponse>  
  
    @POST("logout")  
    suspend fun logout(@Header("Authorization") token: String): Response<Unit>  
  
    @POST("change-password")  
    suspend fun changePassword(  
        @Header("Authorization") token: String,  
        @Body request: ChangePasswordRequest  
    ): Response<MessageResponse>  
  
    @GET("parent/enfants")  
    suspend fun getEnfants(  
        @Header("Authorization") token: String  
    ): Response<EnfantsResponse>  
  
    @GET("eleve/{id}/dashboard")  
    suspend fun getDashboard(  
        @Header("Authorization") token: String,  
        @Path("id") eleveId: Int,  
        @Query("trimestre") trimestre: String = "trimestre1"  
    ): Response<DashboardResponse>  
  
    @GET("eleve/{id}/notes")  
    suspend fun getNotes(  
        @Header("Authorization") token: String,  
        @Path("id") eleveId: Int,  
        @Query("trimestre") trimestre: String = "trimestre1"  
    ): Response<NotesResponse>  
  
    @GET("eleve/{id}/paiements")  
    suspend fun getPaiements(  
        @Header("Authorization") token: String,  
        @Path("id") eleveId: Int  
    ): Response<PaiementsResponse>  
  
    @POST("eleve/{id}/paiements")  
    suspend fun createPaiement(  
        @Header("Authorization") token: String,  
        @Path("id") eleveId: Int,  
        @Body request: CreatePaiementRequest  
    ): Response<PaiementDto>  
  
    @GET("eleve/{id}/absences")  
    suspend fun getAbsences(  
        @Header("Authorization") token: String,  
        @Path("id") eleveId: Int  
    ): Response<AbsencesResponse>  
  
    @GET("eleve/{id}/notifications")  
    suspend fun getNotifications(  
        @Header("Authorization") token: String,  
        @Path("id") eleveId: Int  
    ): Response<NotificationsResponse>  
  
    @POST("eleve/{id}/notifications/{notif}/lire")  
    suspend fun markNotificationRead(  
        @Header("Authorization") token: String,  
        @Path("id") eleveId: Int,  
        @Path("notif") notifId: Int  
    ): Response<MessageResponse>  
}  
