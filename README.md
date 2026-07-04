# SchoolFlow Mobile

Application Android native en Kotlin/Jetpack Compose pour l'espace parent de SchoolFlow.

## Structure

```text
SchoolFlow_mobile/
├── app/src/main/java/com/schoolflow/app/
│   ├── data/              # API Retrofit, modèles, repositories, stockage local
│   ├── ui/                # Écrans Compose, composants réutilisables, thème
│   └── MainActivity.kt    # Navigation principale
├── gradle/                # Version catalog + wrapper
├── build.gradle.kts
├── settings.gradle.kts
└── local.properties.example
```

## Configuration de l'API

Ne modifie plus l'IP directement dans `RetrofitClient.kt`.
Copie le fichier d'exemple :

```bash
cp local.properties.example local.properties
```

Puis adapte `SCHOOLFLOW_API_URL`.

### Émulateur Android Studio

```properties
SCHOOLFLOW_API_URL=http://10.0.2.2:8000/api/
```

Backend Laravel :

```bash
php artisan serve --host=127.0.0.1 --port=8000
```

### Téléphone réel sur le même réseau

```properties
SCHOOLFLOW_API_URL=http://IP_DU_PC:8000/api/
```

Backend Laravel :

```bash
php artisan serve --host=0.0.0.0 --port=8000
```

Tester sur le téléphone avant l'application :

```text
http://IP_DU_PC:8000/up
```

### Téléphone réel via USB + ADB reverse

```bash
adb devices
adb reverse tcp:8000 tcp:8000
```

Puis :

```properties
SCHOOLFLOW_API_URL=http://127.0.0.1:8000/api/
```

Backend Laravel :

```bash
php artisan serve --host=127.0.0.1 --port=8000
```

## Build

```bash
./gradlew clean
./gradlew assembleDebug
```

## Nettoyage effectué

- Suppression des fichiers Laravel qui étaient mélangés au projet mobile.
- Suppression des dossiers générés : `.git`, `.gradle`, `.idea`, `build`, `app/build`.
- Configuration API externalisée dans `local.properties`.
- `RetrofitClient` nettoyé : headers JSON, logs seulement en debug, timeouts cohérents.
- Navigation principale modernisée avec Navigation Compose.
- Écrans principaux retravaillés : connexion, sélection enfant, dashboard.
- Thème Material 3 plus propre : palette bleue, cartes arrondies, états chargement/erreur/empty state.
