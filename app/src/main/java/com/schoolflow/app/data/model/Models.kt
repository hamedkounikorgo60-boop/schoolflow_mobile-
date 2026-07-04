package com.schoolflow.app.data.model  
  
data class LoginRequest(  
    val email: String,  
    val password: String  
)  
  
data class LoginResponse(  
    val token: String,  
    val user: UserDto  
)  
  
data class UserDto(  
    val id: Int,  
    val name: String,  
    val email: String,  
    val role: String,  
    val eleve_id: Int?,  
    val eleves: List<EleveChoixDto>? = null  
)  
  
data class EleveChoixDto(  
    val id: Int,  
    val nom: String,  
    val prenoms: String,  
    val classe: String  
)  
  
data class EleveDto(  
    val id: Int,  
    val nom: String,  
    val prenoms: String,  
    val matricule: String,  
    val classe: String,  
    val photo: String?,  
    val date_naissance: String? = null,  
    val lieu_naissance: String? = null,  
    val genre: String? = null,  
    val telephone: String? = null,  
    val adresse: String? = null,  
    val email: String? = null  
)  
  
data class EcoleDto(  
    val nom: String? = null,  
    val adresse: String? = null,  
    val telephone: String? = null,  
    val email: String? = null,  
    val annee_scolaire: String? = null  
)  
  
data class DashboardResponse(  
    val eleve: EleveDto,  
    val ecole: EcoleDto? = null,  
    val trimestre: String,  
    val moyenne_generale: Double?,  
    val rang: Int,  
    val total_eleves: Int,  
    val dernieres_notes: List<NoteResumeDto>  
)  
  
data class NoteResumeDto(  
    val matiere: String,  
    val note: Double,  
    val coefficient: Int  
)  
  
data class NotesResponse(  
    val notes: List<NoteDto>,  
    val moyenne_generale: Double?  
)  
  
data class NoteDto(  
    val id: Int,  
    val matiere: String,  
    val note: Double,  
    val coefficient: Int,  
    val trimestre: String  
)  
  
data class PaiementsResponse(  
    val paiements: List<PaiementDto>,  
    val total_paye: Double  
)  
  
data class PaiementDto(  
    val id: Int,  
    val recu_numero: String,  
    val montant: Double,  
    val type: String,  
    val trimestre: String,  
    val date: String,  
    val mode: String? = null  
)  
  
data class CreatePaiementRequest(  
    val montant: Double,  
    val type: String,  
    val mode_paiement: String,
    val numero: String
)  
  
data class ChangePasswordRequest(  
    val current_password: String,  
    val new_password: String,  
    val new_password_confirmation: String  
)  
  
data class MessageResponse(  
    val message: String  
)  
  
data class AbsencesResponse(  
    val absences: List<AbsenceDto>,  
    val nb_absences: Int,  
    val nb_retards: Int,  
    val nb_non_justifiees: Int  
)  
  
data class AbsenceDto(  
    val id: Int,  
    val date: String,  
    val type: String,  
    val justifiee: Boolean,  
    val motif: String?  
)  
  
data class NoteDetailDto(  
    val matiere: String,  
    val coefficient: Int,  
    val note: Double  
)  
  
data class EnfantsResponse(  
    val enfants: List<EleveChoixDto>  
)  
  
data class NotificationsResponse(  
    val notifications: List<NotificationDto>,  
    val nb_non_lues: Int  
)  
  
data class NotificationDto(  
    val id: Int,  
    val type: String,  
    val titre: String,  
    val message: String,  
    val lue: Boolean,  
    val date: String  
)  
