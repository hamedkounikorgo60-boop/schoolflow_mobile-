# 📱 SchoolFlow Mobile — Suivi Scolaire Parent-Enfant  
  
[![Kotlin](https://img.shields.io/badge/Kotlin-2.0-purple)](https://kotlinlang.org)  
[![Jetpack Compose](https://img.shields.io/badge/Jetpack%20Compose-Material%203-blue)](https://developer.android.com/jetpack/compose)  
[![Android](https://img.shields.io/badge/Android-minSdk%2024-green)](https://developer.android.com)  
  
## 📌 Sujet  
  
**Application Mobile de Suivi Scolaire Parent-Enfant** — Université Joseph Ki-Zerbo, UFR/SEA.  
  
Application Android native (Kotlin / Jetpack Compose) permettant aux parents d'élèves de suivre en temps réel la scolarité de leurs enfants dans un établissement primaire (du CP1 au CM2) : informations académiques, financières et administratives. Elle consomme l'API REST du backend Laravel **SchoolFlow**.  
  
## 👥 Membres du groupe  
  
| Nom | Rôle |  
|-----|------|  
| Hamed Kounikorgo | Développeur |  
| Sore Abdoulaye | Développeur |  
  
## ✨ Fonctionnalités  
  
### 🔐 Authentification Parent  
- Connexion sécurisée par email et mot de passe (token Sanctum)  
- Déconnexion  
- Modification du mot de passe  
  
### 📊 Tableau de bord de l'élève  
- Informations de base (nom, prénom, photo, classe)  
- Moyenne générale  
- Rang dans la classe  
- Résumé des dernières notes obtenues  
  
### 📝 Consultation des notes  
- Liste des matières et notes par matière  
- Moyennes trimestrielles (sélection par trimestre)  
  
### 💳 Suivi des paiements  
- Historique des versements effectués  
- Montant total payé  
- Déclaration d'un nouveau paiement (Mobile Money, virement)  
- Consultation du numéro de reçu  
  
### 📅 Suivi des absences  
- Liste des absences de l'élève  
- Motifs d'absence (si renseignés)  
  
### 🔔 Notifications et annonces  
- Réception des annonces de l'école  
- Notifications importantes (examens, réunions, échéances de paiement)  
  
## 🛠️ Technologies  
  
| Technologie | Usage |  
|-------------|-------|  
| Kotlin | Langage principal |  
| Jetpack Compose (Material 3) | Interface utilisateur |  
| Navigation Compose | Navigation entre écrans |  
| Retrofit + OkHttp + Gson | Appels réseau vers l'API REST |  
| DataStore Preferences | Stockage local du token et de la session |  
| Coil | Chargement des images (photo élève) |  
| Coroutines | Programmation asynchrone |  
  
## 🏗️ Architecture  
  
Architecture **MVVM** en couches :  
  
\`\`\`text  
UI (Écrans Compose) → ViewModel (uiState) → Repository → Retrofit / DataStore → API Laravel  
\`\`\`  
  
\`\`\`text  
app/src/main/java/com/schoolflow/app/  
├── data/  
│   ├── api/          # RetrofitClient, ApiService  
│   ├── model/        # DTOs (Models.kt)  
│   ├── repository/   # AuthRepository, EleveRepository  
│   └── local/        # TokenManager (DataStore)  
├── ui/               # Écrans Compose, composants réutilisables, thème  
└── MainActivity.kt   # Navigation principale  
\`\`\`  
  
## ⚙️ Installation  
  
### Prérequis  
- Android Studio (Ladybug ou plus récent)  
- JDK 17  
- Le backend Laravel **SchoolFlow** démarré et accessible  
  
### Étapes  
  
1. Cloner le dépôt  
  
        git clone https://github.com/hamedkounikorgo60-boop/schoolflow_mobile-.git  
        cd schoolflow_mobile-  
  
2. Configurer l'URL de l'API  
  
        cp local.properties.example local.properties  
  
   Puis adapter \`SCHOOLFLOW_API_URL\`.  
  
3. Compiler l'application  
  
        ./gradlew clean  
        ./gradlew assembleDebug  
  
4. Lancer l'application depuis Android Studio.  
  
## 🌐 Configuration de l'API  
  
### Émulateur Android Studio  
  
    SCHOOLFLOW_API_URL=http://10.0.2.2:8000/api/  
  
### Téléphone réel sur le même réseau Wi-Fi  
  
    SCHOOLFLOW_API_URL=http://IP_DU_PC:8000/api/  
  
Démarrer le backend :  
  
    php artisan serve --host=0.0.0.0 --port=8000  
  
## 📄 Licence  
  
MIT  
